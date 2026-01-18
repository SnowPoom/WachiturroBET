package controlador;

import modelo.dao.JPAUtil;
import modelo.dao.jpa.BilleteraJPADAO;
import modelo.dao.jpa.MovimientoJPADAO;
import modelo.entidades.Billetera;
import modelo.entidades.Movimiento;
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
import java.time.LocalDate;

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

    // --- PATRÓN RUTEADOR (Solicitado en Arquitectura) ---
    private void ruteador(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Por defecto la ruta es "entrar" (carga inicial), si viene parámetro, se usa ese (ej: "recargar")
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

    // 1. entrar(): Inicia el caso de uso
    private void entrar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Lógica auxiliar para asegurar datos de prueba (manteniendo tu lógica original)
        EntityManager em = JPAUtil.getEntityManager();
        try {
            InitTestDataController.ensureTestUser(req, em);
        } finally {
            em.close();
        }
        
        // Paso 1.1 del diagrama
        this.presentarFormulario(req, resp);
    }

    // 1.1. presentarFormulario(): Muestra la vista (JSP)
    private void presentarFormulario(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher("/jsp/billetera.jsp");
        rd.forward(req, resp);
    }

    // 2. ingresarMontoARecargar(): Captura el input del usuario (Equivalente al inicio del POST)
    private void ingresarMontoARecargar(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        // Abrimos EM para toda la transacción de este método
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Aseguramos que exista un usuario de prueba en sesión si no hay uno (evita fallo cuando la sesión es nueva)
            InitTestDataController.ensureTestUser(req, em);

            UsuarioRegistrado usuario = obtenerUsuarioDeSesionOParametro(req, em);
            
            if (usuario == null) {
                this.presentarErrorAlRecargar(req, resp, "Usuario no identificado.");
                return;
            }

            // Obtener monto del request
            String montoStr = req.getParameter("monto");
            double monto = 0;
            try {
                monto = Double.parseDouble(montoStr);
            } catch (NumberFormatException | NullPointerException e) {
                this.presentarErrorAlRecargar(req, resp, "Formato de monto inválido.");
                return;
            }

            // Paso 2.1 del diagrama: Llamar a procesarRecarga
            this.procesarRecarga(monto, usuario, req, resp, em);

        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // 2.1. procesarRecarga(): Orquesta las validaciones y llamadas a los DAOs
    private void procesarRecarga(double monto, UsuarioRegistrado usuario, HttpServletRequest req, HttpServletResponse resp, EntityManager em) throws IOException, ServletException {
        
    	BilleteraJPADAO billeteraDAO = new BilleteraJPADAO(em);
        // Paso 2.2: validarMonto
        if (!billeteraDAO.validarMonto(monto)) {
            // Paso 2.7 (Alternativo): Presentar error
            this.presentarErrorAlRecargar(req, resp, "El monto debe ser mayor a 0.");
            return;
        }

        // Instanciar DAOs (Según diagrama: billeteraDAO y movimientoDAO)
        MovimientoJPADAO movimientoDAO = new MovimientoJPADAO(em);

        try {
            // Paso 2.3: billeteraDAO.recargarBilletera
            boolean recargaExitosa = billeteraDAO.recargarBilletera(monto, usuario);

            if (recargaExitosa) {
                // Paso 2.5: movimientoDAO.crearMovimiento
                Movimiento mov = new Movimiento();
                mov.setUsuario(usuario);
                mov.setTipo(TipoMovimiento.RECARGA);
                mov.setMonto(monto);
                mov.setFecha(LocalDate.now());
                
                boolean movExitoso = movimientoDAO.crearMovimiento(mov);

                if (movExitoso) {
                    // Actualizar saldo en sesión (Auxiliar)
                    HttpSession session = req.getSession();
                    InitTestDataController.actualizarSaldoEnSesion(session, usuario, em);
                    
                    // Paso 2.6: presentarMensajeConfirmacionRecargar
                    this.presentarMensajeConfirmacionRecargar(req, resp, monto, usuario.getNombre());
                } else {
                    this.presentarErrorAlRecargar(req, resp, "Error al registrar el movimiento.");
                }
            } else {
                this.presentarErrorAlRecargar(req, resp, "Error al actualizar la billetera.");
            }

        } catch (Exception e) {
            this.presentarErrorAlRecargar(req, resp, "Error inesperado: " + e.getMessage());
        }
    }

    // 2.6. presentarMensajeConfirmacionRecargar(): Final feliz
    private void presentarMensajeConfirmacionRecargar(HttpServletRequest req, HttpServletResponse resp, double monto, String nombreUsuario) throws IOException {
        HttpSession session = req.getSession();
        session.setAttribute("flash_status", "OK");
        session.setAttribute("flash_operacion", "RECARGA");
        session.setAttribute("flash_message", "La billetera fue recargada con $" + monto + " para usuario " + nombreUsuario);
        session.setAttribute("flash_monto", monto);
        session.setAttribute("flash_usuarioName", nombreUsuario);
        
        resp.sendRedirect(req.getContextPath() + "/jsp/billetera.jsp");
    }

    // 2.7 y 2.8. presentarErrorAlRecargar(): Final con error
    private void presentarErrorAlRecargar(HttpServletRequest req, HttpServletResponse resp, String mensajeError) throws IOException {
        HttpSession session = req.getSession();
        session.setAttribute("flash_status", "ERROR");
        session.setAttribute("flash_operacion", "RECARGA");
        session.setAttribute("flash_message", mensajeError);
        
        resp.sendRedirect(req.getContextPath() + "/jsp/billetera.jsp");
    }

    // --- MÉTODOS UTILITARIOS (No parte del diagrama pero necesarios para que funcione) ---
    
    private UsuarioRegistrado obtenerUsuarioDeSesionOParametro(HttpServletRequest req, EntityManager em) {
        HttpSession session = req.getSession(false);
        String usuarioIdStr = req.getParameter("usuarioId");
        
        if (session != null && session.getAttribute("currentUser") != null) {
            UsuarioRegistrado uSession = (UsuarioRegistrado) session.getAttribute("currentUser");
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
}