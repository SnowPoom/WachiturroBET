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
public class RetirarFondosController extends HttpServlet {
	private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            ensureTestUser(req, em);
            RequestDispatcher rd = req.getRequestDispatcher("/jsp/billetera.jsp");
            rd.forward(req, resp);
        } finally {
            em.close();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            BilleteraJPADAO billeteraDAO = new BilleteraJPADAO(em);
            MovimientoJPADAO movimientoDAO = new MovimientoJPADAO(em);
            
            String operacion = "RETIRO"; 
            UsuarioRegistrado usuario = obtenerUsuarioDeSesionOParametro(req, em);
            
            if (usuario == null) {
                enviarError(req, resp, "Usuario no identificado.", operacion);
                return;
            }

            Double monto = ingresarMontoARetirar(req);
            if (monto == null || !esMontoValido(monto)) {
                enviarError(req, resp, "El monto ingresado no es válido.", operacion);
                return;
            }

            procesarRetiro(req, monto, usuario, em, billeteraDAO, movimientoDAO);

            // PRG
            resp.sendRedirect(req.getContextPath() + "/jsp/billetera.jsp");
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }
    
    private void procesarRetiro(HttpServletRequest req, double monto, UsuarioRegistrado usuario,
                                EntityManager em, BilleteraJPADAO billeteraDAO, MovimientoJPADAO movimientoDAO) {
        HttpSession session = req.getSession();
        session.setAttribute("flash_operacion", "RETIRO");
        
        try {
            boolean tieneFondos = billeteraDAO.existenFondosValidos(usuario, monto);
            if (!tieneFondos) {
                session.setAttribute("flash_status", "ERROR");
                session.setAttribute("flash_message", "Fondos insuficientes para retirar $" + monto);
                return;
            }

            boolean retirado = billeteraDAO.retirarFondos(monto, usuario);
            if (!retirado) {
                session.setAttribute("flash_status", "ERROR");
                session.setAttribute("flash_message", "Fallo al retirar fondos.");
                return;
            }

            Movimiento mov = new Movimiento();
            mov.setUsuario(usuario);
            mov.setTipo(TipoMovimiento.RETIRO);
            mov.setMonto(monto);
            mov.setFecha(LocalDate.now());

            if (!movimientoDAO.crearMovimiento(mov)) {
                session.setAttribute("flash_status", "ERROR");
                session.setAttribute("flash_message", "Retiro exitoso, pero error al registrar movimiento.");
                return;
            }

            session.setAttribute("flash_status", "OK");
            session.setAttribute("flash_message", "Se retiraron $" + monto + " correctamente.");
            session.setAttribute("flash_monto", monto);
            session.setAttribute("flash_usuarioName", usuario.getNombre());

            actualizarSaldoEnSesion(req, usuario, em);

        } catch (Exception e) {
            session.setAttribute("flash_status", "ERROR");
            session.setAttribute("flash_message", "Error inesperado en retiro: " + e.getMessage());
        }
    }
    
    // --- MÉTODOS AUXILIARES COPIADOS Y ADAPTADOS PARA RECIBIR EM ---
    
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
            return em.find(UsuarioRegistrado.class, u.getId()); // Re-attach
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
                em.getEntityManagerFactory().getCache().evictAll(); // Limpiar cache
                TypedQuery<Double> q = em.createQuery("SELECT b.saldo FROM Billetera b WHERE b.usuario.id = :uid", Double.class);
                q.setParameter("uid", usuario.getId());
                Double saldo = q.getSingleResult();
                session.setAttribute("currentUserSaldo", saldo != null ? saldo : 0.0);
            } catch (Exception ex) { 
                ex.printStackTrace();
            }
        }
    }
    
    private Double ingresarMontoARetirar(HttpServletRequest req) {
        String montoStr = req.getParameter("monto");
        if (montoStr == null) return null;
        try { return Double.parseDouble(montoStr); } catch (NumberFormatException nfe) { return null; }
    }
    
    private boolean esMontoValido(double monto) { return monto > 0; }

    private void enviarError(HttpServletRequest req, HttpServletResponse resp, String mensaje, String operacion) throws ServletException, IOException {
        HttpSession session = req.getSession();
        session.setAttribute("flash_status", "ERROR");
        session.setAttribute("flash_operacion", operacion);
        session.setAttribute("flash_message", mensaje);
        resp.sendRedirect(req.getContextPath() + "/jsp/billetera.jsp");
    }
}