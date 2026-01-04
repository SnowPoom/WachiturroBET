package controlador;

import modelo.dao.JPAUtil;
import modelo.dao.jpa.BilleteraJPADAO;
import modelo.dao.jpa.MovimientoJPADAO;
import modelo.entidades.Billetera;
import modelo.entidades.Movimiento;
import modelo.entidades.TipoMovimiento;
import modelo.entidades.UsuarioRegistrado;

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
import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/recargarBilletera")
public class RecargarBilleteraController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // YA NO declaramos DAOs ni EntityManager aquí como atributos de clase
    // para evitar problemas de caché y concurrencia.

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
        // Abrimos el EntityManager EXCLUSIVAMENTE para esta petición
        EntityManager em = JPAUtil.getEntityManager();
        
        try {
            // Si no hay usuario en sesión, crear/asegurar el usuario de test
            HttpSession sessionCheck = req.getSession(false);
            if (sessionCheck == null || sessionCheck.getAttribute("currentUser") == null) {
                ensureTestUser(req, em);
            }

            // Instanciamos los DAOs con este EM fresco
            BilleteraJPADAO billeteraDAO = new BilleteraJPADAO(em);
            MovimientoJPADAO movimientoDAO = new MovimientoJPADAO(em);
            
            String operacion = req.getParameter("operacion");
            if (operacion == null) operacion = "RECARGA";
            
            UsuarioRegistrado usuario = obtenerUsuarioDeSesionOParametro(req, em);
            
            if (usuario == null) {
                enviarError(req, resp, "Usuario no identificado.", operacion);
                return;
            }

            Double monto = ingresarMontoARecargar(req);
            if (monto == null || !validarMonto(monto)) {
                enviarError(req, resp, "El monto ingresado no es válido.", operacion);
                return;
            }

            if ("RECARGA".equalsIgnoreCase(operacion)) {
                boolean exitoso = procesarRecarga(req, monto, usuario, em, billeteraDAO, movimientoDAO);
                // Se podrían tomar acciones adicionales según 'exitoso', pero la lógica de flash ya está en el método.
            }

            // PRG: Redirect final
            resp.sendRedirect(req.getContextPath() + "/jsp/billetera.jsp");
            
        } finally {
            // IMPORTANTE: Cerrar el EM para limpiar caché y liberar la conexión
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // --- LÓGICA REFACTORIZADA PARA RECIBIR EntityManager ---

    private boolean procesarRecarga(HttpServletRequest req, double monto, UsuarioRegistrado usuario, 
                                 EntityManager em, BilleteraJPADAO billeteraDAO, MovimientoJPADAO movimientoDAO) {
        HttpSession session = req.getSession();
        session.setAttribute("flash_operacion", "RECARGA");

        try {
            boolean recargado = billeteraDAO.recargarBilletera(monto, usuario);
            
            if (!recargado) {
                session.setAttribute("flash_status", "ERROR");
                session.setAttribute("flash_message", "Fallo al recargar la billetera.");
                return false;
            }

            Movimiento mov = new Movimiento();
            mov.setUsuario(usuario);
            mov.setTipo(TipoMovimiento.RECARGA);
            mov.setMonto(monto);
            mov.setFecha(LocalDate.now());

            boolean movGuardado = movimientoDAO.crearMovimiento(mov);
            
            if (!movGuardado) {
                session.setAttribute("flash_status", "ERROR");
                session.setAttribute("flash_message", "Recarga exitosa, pero error al registrar movimiento.");
                return false;
            }

            String msg = "La billetera fue recargada con $" + monto + " para usuario " + usuario.getNombre();
            session.setAttribute("flash_status", "OK");
            session.setAttribute("flash_message", msg);
            session.setAttribute("flash_monto", monto);
            session.setAttribute("flash_usuarioName", usuario.getNombre());

            actualizarSaldoEnSesion(req, usuario, em);

            return true;

        } catch (Exception e) {
            session.setAttribute("flash_status", "ERROR");
            session.setAttribute("flash_message", "Error inesperado: " + e.getMessage());
            return false;
        }
    }

    private UsuarioRegistrado obtenerUsuarioDeSesionOParametro(HttpServletRequest req, EntityManager em) {
        HttpSession session = req.getSession(false);
        String usuarioIdStr = req.getParameter("usuarioId");
        
        // Aunque esté en sesión, es mejor recargarlo del EM actual para evitar "Detached Entity"
        if (session != null && session.getAttribute("currentUser") != null) {
            UsuarioRegistrado uSession = (UsuarioRegistrado) session.getAttribute("currentUser");
            // Lo buscamos en la DB para asegurarnos que está conectado a este EM
            return em.find(UsuarioRegistrado.class, uSession.getId());
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
                // Forzamos limpiar caché antes de consultar saldo
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

    // Métodos auxiliares simples
    private Double ingresarMontoARecargar(HttpServletRequest req) {
        String montoStr = req.getParameter("monto");
        if (montoStr == null) return null;
        try { return Double.parseDouble(montoStr); } catch (NumberFormatException nfe) { return null; }
    }

    private boolean validarMonto(double monto) { return monto > 0; }

    private void enviarError(HttpServletRequest req, HttpServletResponse resp, String mensaje, String operacion) throws ServletException, IOException {
        HttpSession session = req.getSession();
        session.setAttribute("flash_status", "ERROR");
        session.setAttribute("flash_operacion", operacion);
        session.setAttribute("flash_message", mensaje);
        resp.sendRedirect(req.getContextPath() + "/jsp/billetera.jsp");
    }
}