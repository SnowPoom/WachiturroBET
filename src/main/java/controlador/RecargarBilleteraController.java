package controlador;

import modelo.dao.JPAUtil;
import modelo.dao.jpa.BilleteraJPADAO;
import modelo.dao.jpa.MovimientoJPADAO;
import modelo.entidades.Recarga;
import modelo.entidades.TipoMovimiento;
import modelo.entidades.UsuarioRegistrado;

import jakarta.persistence.EntityManager;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.math.RoundingMode;

@WebServlet("/recargarBilletera")
public class RecargarBilleteraController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.ruteador(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.ruteador(req, resp);
    }

    // --- PATRÓN RUTEADOR ---
    private void ruteador(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ruta = (req.getParameter("ruta") != null) ? req.getParameter("ruta") : "entrar";

        switch (ruta) {
            case "entrar":
                this.entrar(req, resp);
                break;
            case "recargar":
                this.ingresarMontoARecargar(req, resp);
                break;
            default:
                this.entrar(req, resp);
                break;
        }
    }

    // --- MÉTODOS DEL DIAGRAMA DE SECUENCIA ---

    // 1. entrar()
    private void entrar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // ELIMINADO: Ya no llamamos a InitTestDataController.ensureTestUser(req, em);
        // La validación de usuario se hace en el JSP o al intentar recargar.
        
        this.presentarFormulario(req, resp);
    }

    // 1.1. presentarFormulario()
    private void presentarFormulario(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher("/jsp/billetera.jsp");
        rd.forward(req, resp);
    }

    // 2. ingresarMontoARecargar()
    private void ingresarMontoARecargar(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            UsuarioRegistrado usuario = obtenerUsuarioDeSesionOParametro(req, em);
            
            if (usuario == null) {
                this.presentarErrorAlRecargar(req, resp, "Usuario no identificado. Por favor inicie sesión.");
                return;
            }

            String montoStr = req.getParameter("monto");
            if (montoStr == null) {
                this.presentarErrorAlRecargar(req, resp, "Formato de monto inválido.");
                return;
            }

            montoStr = montoStr.trim().replace(',', '.');
            if (!montoStr.matches("^\\d+(\\.\\d{1,2})?$") ) {
                this.presentarErrorAlRecargar(req, resp, "El monto debe tener como máximo 2 decimales y ser positivo.");
                return;
            }

            double monto = 0;
            try {
                BigDecimal bd = new BigDecimal(montoStr);
                bd = bd.setScale(2, RoundingMode.HALF_UP);
                monto = bd.doubleValue();
            } catch (NumberFormatException | ArithmeticException e) {
                this.presentarErrorAlRecargar(req, resp, "Formato de monto inválido.");
                return;
            }

            this.procesarRecarga(monto, usuario, req, resp, em);

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // 2.1. procesarRecarga()
    private void procesarRecarga(double monto, UsuarioRegistrado usuario, HttpServletRequest req, HttpServletResponse resp, EntityManager em) throws IOException, ServletException {
        BilleteraJPADAO billeteraDAO = new BilleteraJPADAO(em);
        
        if (!billeteraDAO.validarMonto(monto)) {
            this.presentarErrorAlRecargar(req, resp, "El monto debe ser mayor a 0.");
            return;
        }

        MovimientoJPADAO movimientoDAO = new MovimientoJPADAO(em);

        try {
            boolean recargaExitosa = billeteraDAO.recargarBilletera(monto, usuario);

            if (recargaExitosa) {
                Recarga mov = new Recarga();
                mov.setUsuario(usuario);
                mov.setTipo(TipoMovimiento.RECARGA);
                mov.setMonto(monto);
                mov.setFecha(LocalDateTime.now());
                
                boolean movExitoso = movimientoDAO.crearMovimiento(mov);

                if (movExitoso) {
                    // Actualizar saldo en sesión (Método local ahora)
                    HttpSession session = req.getSession();
                    this.actualizarSaldoEnSesion(session, usuario, em); 
                    
                    this.presentarMensajeConfirmacionRecargar(req, resp, monto, usuario.getNombre());
                } else {
                    this.presentarErrorAlRecargar(req, resp, "Error al registrar el movimiento.");
                }
            } else {
                this.presentarErrorAlRecargar(req, resp, "Error al actualizar la billetera.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.presentarErrorAlRecargar(req, resp, "Error inesperado: " + e.getMessage());
        }
    }

    // --- MÉTODO AGREGADO (Reemplaza al de InitTestDataController) ---
    private void actualizarSaldoEnSesion(HttpSession session, UsuarioRegistrado usuario, EntityManager em) {
        if (session != null) {
            try {
                // Limpiamos caché L1 para asegurar dato fresco de la BD
                em.getEntityManagerFactory().getCache().evict(modelo.entidades.Billetera.class);
                
                String jpql = "SELECT b.saldo FROM Billetera b WHERE b.usuario.id = :uid";
                Double saldo = em.createQuery(jpql, Double.class)
                                 .setParameter("uid", usuario.getId())
                                 .getSingleResult();
                
                session.setAttribute("currentUserSaldo", saldo != null ? saldo : 0.0);
            } catch (Exception ex) {
                ex.printStackTrace();
                session.setAttribute("currentUserSaldo", 0.0); // Fallback
            }
        }
    }

    // 2.6. presentarMensajeConfirmacionRecargar()
    private void presentarMensajeConfirmacionRecargar(HttpServletRequest req, HttpServletResponse resp, double monto, String nombreUsuario) throws IOException {
        HttpSession session = req.getSession();
        session.setAttribute("flash_status", "OK");
        session.setAttribute("flash_operacion", "RECARGA");
        session.setAttribute("flash_message", "La billetera fue recargada con $" + monto); // Mensaje simplificado
        session.setAttribute("flash_monto", monto);
        session.setAttribute("flash_usuarioName", nombreUsuario);
        
        resp.sendRedirect(req.getContextPath() + "/jsp/billetera.jsp");
    }

    // 2.7 y 2.8. presentarErrorAlRecargar()
    private void presentarErrorAlRecargar(HttpServletRequest req, HttpServletResponse resp, String mensajeError) throws IOException {
        HttpSession session = req.getSession();
        session.setAttribute("flash_status", "ERROR");
        session.setAttribute("flash_operacion", "RECARGA");
        session.setAttribute("flash_message", mensajeError);
        
        resp.sendRedirect(req.getContextPath() + "/jsp/billetera.jsp");
    }

    // --- MÉTODOS UTILITARIOS ---
    
    private UsuarioRegistrado obtenerUsuarioDeSesionOParametro(HttpServletRequest req, EntityManager em) {
        HttpSession session = req.getSession(false);
        String usuarioIdStr = req.getParameter("usuarioId");
        
        // 1. Prioridad: Usuario en Sesión
        if (session != null && session.getAttribute("usuario") != null) {
            // Nota: En IniciarSesionController guardaste el atributo como "usuario"
            Object obj = session.getAttribute("usuario");
            if (obj instanceof UsuarioRegistrado) {
                 UsuarioRegistrado uSession = (UsuarioRegistrado) obj;
                 return em.find(UsuarioRegistrado.class, uSession.getId());
            }
        } 
        
        // 2. Fallback: Usuario por parámetro (útil para pruebas, pero inseguro en prod sin validación)
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
}