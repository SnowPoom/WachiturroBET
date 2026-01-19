package controlador;

import java.io.IOException;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
import modelo.entidades.Retiro;
import modelo.entidades.TipoMovimiento;
import modelo.entidades.UsuarioRegistrado;

// CAMBIO 1: Usamos el nombre de la clase como URL (estándar del profe)
@WebServlet("/retirarFondos")
public class RetirarFondosController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public RetirarFondosController() {
        super();
    }

    // --- EL FRONT CONTROLLER (Router) ---

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.ruteador(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.ruteador(req, resp);
    }

    private void ruteador(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Por defecto, si no hay ruta, es "entrar" (inicio del caso de uso)
        String ruta = (req.getParameter("ruta") == null) ? "entrar" : req.getParameter("ruta");

        switch (ruta) {
            case "entrar":
                this.entrar(req, resp);
                break;
            case "retirar":
                this.retirarFondos(req, resp);
                break;
            default:
                this.entrar(req, resp);
                break;
        }
    }

    // --- MÉTODOS DEL DIAGRAMA DE SECUENCIA ---

    /**
     * Flecha 1 del diagrama: entrar()
     */
    private void entrar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            ensureTestUser(req, em);
            
            // Flecha 1.1 del diagrama: presentarFormulario()
            this.presentarFormulario(req, resp);
            
        } finally {
            if (em != null) em.close();
        }
    }

    /**
     * Flecha 2.2 del diagrama: retirarFondos(...)
     * Este método orquesta la lógica de negocio (Valida y llama al DAO)
     */
    private void retirarFondos(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            HttpSession session = req.getSession();
            session.setAttribute("flash_operacion", "RETIRO");

            // 1. Obtener Parámetros
            UsuarioRegistrado usuario = obtenerUsuarioDeSesionOParametro(req, em);
            if (usuario == null) {
                presentarMensajeErrorRetirar(req, resp, "Usuario no identificado.");
                return;
            }

            // Flecha 2: ingresarMontoARetirar(...)
            Double monto = ingresarMontoARetirar(req);

            // Flecha 2.1: verificarMontoARetirar (Validación básica)
            if (monto == null || monto <= 0) {
                presentarMensajeErrorRetirar(req, resp, "El monto ingresado no es válido.");
                return;
            }

            // Instanciar DAOs
            BilleteraJPADAO billeteraDAO = new BilleteraJPADAO(em);
            MovimientoJPADAO movimientoDAO = new MovimientoJPADAO(em);

            // Verificación de Fondos (Lógica del DAO)
            boolean tieneFondos = billeteraDAO.existenFondosValidos(usuario, monto);
            if (!tieneFondos) {
                // Flecha 2.7 (Caso alterno): presentarMensajeErrorRetirar
                presentarMensajeErrorRetirar(req, resp, "Fondos insuficientes.");
                return;
            }

            // Ejecución del Retiro
            boolean retirado = billeteraDAO.retirarFondos(monto, usuario);
            if (!retirado) {
                presentarMensajeErrorRetirar(req, resp, "Fallo técnico al retirar fondos.");
                return;
            }

            // Flecha 2.4: crearMovimiento(...)
            Retiro mov = new Retiro();
            mov.setUsuario(usuario);
            mov.setTipo(TipoMovimiento.RETIRO);
            mov.setMonto(monto);
            mov.setFecha(java.time.LocalDateTime.now());

            if (!movimientoDAO.crearMovimiento(mov)) {
                presentarMensajeErrorRetirar(req, resp, "Error al registrar el historial.");
                return;
            }

            // Actualizar sesión para la vista
            actualizarSaldoEnSesion(req, usuario, em);

            // Flecha 2.6: presentarMensajeConfirmacionRetiro()
            presentarMensajeConfirmacionRetiro(req, resp, monto, usuario.getNombre());

        } catch (Exception e) {
            e.printStackTrace();
            presentarMensajeErrorRetirar(req, resp, "Error inesperado: " + e.getMessage());
        } finally {
            if (em != null) em.close();
        }
    }

    // --- MÉTODOS DE VISTA (Boundaries del diagrama) ---

    /**
     * Flecha 1.1: presentarFormulario()
     */
    private void presentarFormulario(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // En el diagrama esto va hacia "billetera : Billetera" (La vista)
        RequestDispatcher rd = req.getRequestDispatcher("/jsp/billetera.jsp");
        rd.forward(req, resp);
    }

    /**
     * Flecha 2.6: presentarMensajeConfirmacionRetiro()
     */
    private void presentarMensajeConfirmacionRetiro(HttpServletRequest req, HttpServletResponse resp, double monto, String nombreUsuario) throws ServletException, IOException {
        HttpSession session = req.getSession();
        session.setAttribute("flash_status", "OK");
        session.setAttribute("flash_message", "Se retiraron $" + monto + " correctamente.");
        session.setAttribute("flash_monto", monto);
        session.setAttribute("flash_usuarioName", nombreUsuario);
        
        // Redirigir para evitar resubmission (PRG Pattern) pero volviendo a entrar
        resp.sendRedirect("retirarFondos?ruta=entrar");
    }

    /**
     * Flecha 2.7: presentarMensajeErrorRetirar()
     */
    private void presentarMensajeErrorRetirar(HttpServletRequest req, HttpServletResponse resp, String mensaje) throws ServletException, IOException {
        HttpSession session = req.getSession();
        session.setAttribute("flash_status", "ERROR");
        session.setAttribute("flash_message", mensaje);
        
        resp.sendRedirect("retirarFondos?ruta=entrar");
    }

    // --- MÉTODOS AUXILIARES (Lógica interna, igual que antes) ---

    private Double ingresarMontoARetirar(HttpServletRequest req) {
        String montoStr = req.getParameter("monto");
        if (montoStr == null) return null;
        montoStr = montoStr.trim().replace(',', '.');
        // Validar que tenga máxima 2 decimales y sea numérico positivo
        if (!montoStr.matches("^\\d+(\\.\\d{1,2})?$")) return null;
        try {
            BigDecimal bd = new BigDecimal(montoStr);
            bd = bd.setScale(2, RoundingMode.HALF_UP);
            return bd.doubleValue();
        } catch (NumberFormatException | ArithmeticException e) {
            return null;
        }
    }

    private void ensureTestUser(HttpServletRequest req, EntityManager em) {
        HttpSession session = req.getSession();
        if (session.getAttribute("currentUser") != null) return; 

        final String testEmail = "test@example.com";
        EntityTransaction tx = null;
        try {
            TypedQuery<UsuarioRegistrado> q = em.createQuery("SELECT u FROM UsuarioRegistrado u WHERE u.correo = :correo", UsuarioRegistrado.class);
            q.setParameter("correo", testEmail);
            UsuarioRegistrado usuario = null;
            try {
                usuario = q.getSingleResult();
            } catch (NoResultException nre) {
                tx = em.getTransaction();
                tx.begin();
                usuario = new UsuarioRegistrado();
                usuario.setNombre("Usuario");
                usuario.setApellido("Prueba");
                usuario.setCorreo(testEmail);
                usuario.setClave("password");
                em.persist(usuario);
                Billetera billetera = new Billetera();
                billetera.setSaldo(100.0); 
                billetera.setUsuario(usuario);
                em.persist(billetera);
                tx.commit();
            }
            if (usuario != null) {
                actualizarSaldoEnSesion(req, usuario, em);
                session.setAttribute("currentUser", usuario);
            }
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    private UsuarioRegistrado obtenerUsuarioDeSesionOParametro(HttpServletRequest req, EntityManager em) {
        HttpSession session = req.getSession(false);
        String usuarioIdStr = req.getParameter("usuarioId");
        if (session != null && session.getAttribute("currentUser") != null) {
            UsuarioRegistrado u = (UsuarioRegistrado) session.getAttribute("currentUser");
            return em.find(UsuarioRegistrado.class, u.getId());
        } 
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
    
    private void actualizarSaldoEnSesion(HttpServletRequest req, UsuarioRegistrado usuario, EntityManager em) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            try {
                em.getEntityManagerFactory().getCache().evictAll();
                TypedQuery<Double> q = em.createQuery("SELECT b.saldo FROM Billetera b WHERE b.usuario.id = :uid", Double.class);
                q.setParameter("uid", usuario.getId());
                Double saldo = q.getSingleResult();
                session.setAttribute("currentUserSaldo", saldo != null ? saldo : 0.0);
            } catch (Exception ex) { 
                ex.printStackTrace();
            }
        }
    }
}
