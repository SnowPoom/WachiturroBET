package controlador;

import java.io.IOException;
import java.time.LocalDate;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.dao.JPAUtil;
import modelo.dao.jpa.BilleteraJPADAO;
import modelo.dao.jpa.MovimientoJPADAO;
import modelo.entidades.Billetera;
import modelo.entidades.Movimiento;
import modelo.entidades.TipoMovimiento;
import modelo.entidades.UsuarioRegistrado;

@WebServlet("/retirarBilletera")
public class RetiroBilleteraController extends HttpServlet {
	private static final long serialVersionUID = 1L;

    private BilleteraJPADAO billeteraDAO;
    private MovimientoJPADAO movimientoDAO;
    private EntityManager em;
    
    @Override
    public void init() throws ServletException {
        super.init();
        this.em = JPAUtil.getEntityManager();
        this.billeteraDAO = new BilleteraJPADAO(em);
        this.movimientoDAO = new MovimientoJPADAO(em);
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Simular usuario logeado: si no existe en sesión, crearlo y persistirlo
        ensureTestUser(req);

        // Presentar formulario via JSP
        RequestDispatcher rd = req.getRequestDispatcher("/jsp/billetera.jsp");
        rd.forward(req, resp);
    }
    
    /**
     * Ensure a test user exists in the DB and is stored in the HTTP session as 'currentUser'.
     */
    private void ensureTestUser(HttpServletRequest req) {
        HttpSession session = req.getSession();
        Object cur = session.getAttribute("currentUser");
        if (cur != null) return; // already simulated

        final String testEmail = "test@example.com";
        EntityTransaction tx = null;
        try {
            // Try to find existing user by correo
            TypedQuery<UsuarioRegistrado> q = em.createQuery("SELECT u FROM UsuarioRegistrado u WHERE u.correo = :correo", UsuarioRegistrado.class);
            q.setParameter("correo", testEmail);
            UsuarioRegistrado usuario = null;
            try {
                usuario = q.getSingleResult();
            } catch (NoResultException nre) {
                // Not found, create
                tx = em.getTransaction();
                tx.begin();
                usuario = new UsuarioRegistrado();
                usuario.setNombre("Usuario");
                usuario.setApellido("Prueba");
                usuario.setCorreo(testEmail);
                usuario.setClave("password");
                // Persist the new user
                em.persist(usuario);

                // Create a billetera for the user
                Billetera billetera = new Billetera();
                billetera.setSaldo(100.0); // saldo inicial de prueba
                billetera.setUsuario(usuario);
                em.persist(billetera);

                tx.commit();
            }

            // Refresh managed entity to ensure id populated
            if (usuario != null) {
                if (!em.contains(usuario)) {
                    usuario = em.find(UsuarioRegistrado.class, usuario.getId());
                }
                try {
                    TypedQuery<Double> q2 = em.createQuery("SELECT b.saldo FROM Billetera b WHERE b.usuario.id = :uid", Double.class);
                    q2.setParameter("uid", usuario.getId());
                    Double saldo = q2.getSingleResult();
                    session.setAttribute("currentUserSaldo", saldo != null ? saldo : 0.0);
                } catch (Exception ex) {
                    session.setAttribute("currentUserSaldo", 0.0);
                }

                session.setAttribute("currentUser", usuario);
            }
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1. Configuración inicial 
        String operacion = "RETIRO"; 
        
        // 2. Obtener Usuario 
        UsuarioRegistrado usuario = obtenerUsuarioDeSesionOParametro(req);
        
        if (usuario == null) {
            enviarError(req, resp, "Usuario no identificado.", operacion);
            return;
        }

        // 3. Obtener Monto 
        Double monto = ingresarMonto(req);

        if (monto == null || !validarMonto(monto)) {
            enviarError(req, resp, "El monto ingresado no es válido.", operacion);
            return;
        }

        // 4. Procesar el RETIRO
        procesarRetiro(req, monto, usuario);

        // 5. Redirección final
        req.getRequestDispatcher("/jsp/billetera.jsp").forward(req, resp);
    }
    
    /**
     * Método encargado de extraer y parsear el monto.
     * Corresponde al mensaje "ingresarMontoARecargar" del diagrama.
     */
    private Double ingresarMonto(HttpServletRequest req) {
        String montoStr = req.getParameter("monto");
        if (montoStr == null) return null;
        try {
            return Double.parseDouble(montoStr);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }
    
    /**
     * Lógica de retiro .
     */
    private void procesarRetiro(HttpServletRequest req, double monto, UsuarioRegistrado usuario) {
        req.setAttribute("operacion", "RETIRO");
        
        try {
            boolean tieneFondos = billeteraDAO.verificarFondos(usuario, monto);
            if (!tieneFondos) {
                req.setAttribute("status", "ERROR");
                req.setAttribute("message", "Fondos insuficientes para retirar $" + monto);
                return;
            }

            boolean retirado = billeteraDAO.retirarFondos(monto, usuario);
            if (!retirado) {
                req.setAttribute("status", "ERROR");
                req.setAttribute("message", "Fallo al retirar fondos.");
                return;
            }

            Movimiento mov = new Movimiento();
            mov.setUsuario(usuario);
            mov.setTipo(TipoMovimiento.RETIRO);
            mov.setMonto(monto);
            mov.setFecha(LocalDate.now());

            if (!movimientoDAO.crearMovimiento(mov)) {
                req.setAttribute("status", "ERROR");
                req.setAttribute("message", "Retiro exitoso, pero error al registrar movimiento.");
                return;
            }

            req.setAttribute("status", "OK");
            req.setAttribute("message", "Se retiraron $" + monto + " correctamente.");
            req.setAttribute("monto", monto);
            req.setAttribute("usuarioName", usuario.getNombre());

            actualizarSaldoEnSesion(req, usuario);

        } catch (Exception e) {
            req.setAttribute("status", "ERROR");
            req.setAttribute("message", "Error inesperado en retiro: " + e.getMessage());
        }
    }
    
 // -------------------------------------------------------------------------
    // MÉTODOS AUXILIARES
    // -------------------------------------------------------------------------

    private UsuarioRegistrado obtenerUsuarioDeSesionOParametro(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        String usuarioIdStr = req.getParameter("usuarioId");
        
        // Prioridad 1: Usuario en sesión
        if (session != null && session.getAttribute("currentUser") != null) {
            return (UsuarioRegistrado) session.getAttribute("currentUser");
        } 
        
        // Prioridad 2: ID por parámetro (para pruebas o API)
        if (usuarioIdStr != null) {
            try {
                int uid = Integer.parseInt(usuarioIdStr);
                return em.find(UsuarioRegistrado.class, uid);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }
    
    private void actualizarSaldoEnSesion(HttpServletRequest req, UsuarioRegistrado usuario) {
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("currentUser") != null) {
            UsuarioRegistrado suser = (UsuarioRegistrado) session.getAttribute("currentUser");
            if (suser.getId() == usuario.getId()) {
                try {
                    TypedQuery<Double> q = em.createQuery("SELECT b.saldo FROM Billetera b WHERE b.usuario.id = :uid", Double.class);
                    q.setParameter("uid", usuario.getId());
                    Double saldo = q.getSingleResult();
                    session.setAttribute("currentUserSaldo", saldo != null ? saldo : 0.0);
                } catch (Exception ex) { 
                    /* Logear error si es necesario */ 
                }
            }
        }
    }
    
    private boolean validarMonto(double monto) {
        return monto > 0;
    }

    private void enviarError(HttpServletRequest req, HttpServletResponse resp, String mensaje, String operacion) throws ServletException, IOException {
        req.setAttribute("status", "ERROR");
        req.setAttribute("operacion", operacion);
        req.setAttribute("message", mensaje);
        req.getRequestDispatcher("/jsp/billetera.jsp").forward(req, resp);
    }

    @Override
    public void destroy() {
        if (em != null && em.isOpen()) em.close();
        JPAUtil.close();
        super.destroy();
    
    }
}
