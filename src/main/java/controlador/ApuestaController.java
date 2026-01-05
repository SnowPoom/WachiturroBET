package controlador;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.dao.JPAUtil;
import modelo.dao.jpa.ApuestaJPADAO;
import modelo.dao.jpa.EventoJPADAO;
import modelo.dao.jpa.PronosticoJPADAO;
import modelo.dao.jpa.BilleteraJPADAO;
import modelo.entidades.Evento;
import modelo.entidades.Pronostico;
import modelo.entidades.UsuarioRegistrado;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@WebServlet("/apuesta")
public class ApuestaController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Soporta la acción seleccionarEvento
        String action = req.getParameter("action");
        if ("seleccionarEvento".equalsIgnoreCase(action)) {
            seleccionarEvento(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("ingresarMonto".equalsIgnoreCase(action)) {
            ingresarMonto(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    private void seleccionarEvento(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String idStr = req.getParameter("idEvento");
            if (idStr == null) {
                resp.sendRedirect(req.getContextPath() + "/jsp/index.jsp");
                return;
            }
            int id = Integer.parseInt(idStr);
            EventoJPADAO eventoDAO = new EventoJPADAO(em);
            PronosticoJPADAO pronosticoDAO = new PronosticoJPADAO(em);

            // Obtener detalles evento
            Evento evento = eventoDAO.consultarDetallesEvento(id);
            // Obtener pronosticos
            List<Pronostico> pronosticos = pronosticoDAO.obtenerPronosticosPorEvento(evento);

            req.setAttribute("eventoDetalle", evento);
            req.setAttribute("pronosticos", pronosticos);

            RequestDispatcher rd = req.getRequestDispatcher("/jsp/detalleEvento.jsp");
            rd.forward(req, resp);
        } catch (NumberFormatException nfe) {
            resp.sendRedirect(req.getContextPath() + "/jsp/index.jsp");
        } finally {
            em.close();
        }
    }

    private void ingresarMonto(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            HttpSession session = req.getSession(false);
            if (session == null || session.getAttribute("currentUser") == null) {
                session = req.getSession(true);
                session.setAttribute("flash_status", "ERROR");
                session.setAttribute("flash_message", "Usuario no autenticado.");
                resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
                return;
            }

            UsuarioRegistrado usuario = (UsuarioRegistrado) session.getAttribute("currentUser");
            // Reload managed user with current EM to avoid detached issues
            usuario = em.find(UsuarioRegistrado.class, usuario.getId());

            String montoStr = req.getParameter("monto");
            String idPronosticoStr = req.getParameter("idPronostico");
            String idEventoStr = req.getParameter("idEvento");

            Double monto = null;
            try { monto = Double.parseDouble(montoStr); } catch (Exception e) { monto = null; }

            // Validación 1: monto > 0
            if (monto == null || monto <= 0) {
                session.setAttribute("flash_status", "ERROR");
                session.setAttribute("flash_operacion", "APOSTAR");
                session.setAttribute("flash_message", "mostrarMontoInvalido");
                // PRG: redirigir al detalle del evento para mostrar el mensaje
                String redirectUrl = req.getContextPath() + "/apuesta?action=seleccionarEvento";
                if (idEventoStr != null) redirectUrl += "&idEvento=" + idEventoStr;
                resp.sendRedirect(redirectUrl);
                return;
            }

            // Validación 2: existen fondos
            BilleteraJPADAO billeteraDAO = new BilleteraJPADAO(em);
            boolean tieneFondos = billeteraDAO.existenFondosValidos(usuario, monto);
            if (!tieneFondos) {
                session.setAttribute("flash_status", "ERROR");
                session.setAttribute("flash_operacion", "APOSTAR");
                session.setAttribute("flash_message", "mostrarFondosInsuficientes");
                resp.sendRedirect(req.getContextPath() + "/jsp/MensajeErrorBilletera.jsp");
                return;
            }

            // Todo OK: proceder a apostar
            Pronostico pronostico = null;
            if (idPronosticoStr != null) {
                try {
                    int pid = Integer.parseInt(idPronosticoStr);
                    // Suponiendo que Pronostico es una entidad mapeada
                    pronostico = em.find(Pronostico.class, pid);
                } catch (NumberFormatException ignored) {}
            }

            ApuestaJPADAO apuestaDAO = new ApuestaJPADAO(em);
            // La lógica de deduct y persist del movimiento ahora se realiza en apostar() dentro de ApuestaJPADAO
            apuestaDAO.apostar(monto, pronostico, usuario);

            // Actualizar saldo en la sesión consultando la billetera en este EM
            try {
                TypedQuery<Double> q = em.createQuery("SELECT b.saldo FROM Billetera b WHERE b.usuario.id = :uid", Double.class);
                q.setParameter("uid", usuario.getId());
                Double saldo = q.getSingleResult();
                session.setAttribute("currentUserSaldo", saldo != null ? saldo : 0.0);
            } catch (Exception ex) {
                // ignorar, no crítico
                ex.printStackTrace();
            }

            // Si llegamos aquí, la transacción dentro de apostar fue exitosa

            // Finalmente redirigir confirmación
            session.setAttribute("flash_status", "OK");
            session.setAttribute("flash_operacion", "APOSTA");
            session.setAttribute("flash_message", "Apuesta realizada correctamente");
            // PRG: redirigir al detalle del mismo evento para mostrar confirmación
            String redirectUrl = req.getContextPath() + "/apuesta?action=seleccionarEvento";
            if (idEventoStr != null) redirectUrl += "&idEvento=" + idEventoStr;
            resp.sendRedirect(redirectUrl);

        } catch (IllegalStateException ise) {
            // Fondos insuficientes realizado en la capa DAO
            HttpSession session = req.getSession();
            session.setAttribute("flash_status", "ERROR");
            session.setAttribute("flash_operacion", "APOSTAR");
            session.setAttribute("flash_message", "mostrarFondosInsuficientes");
            String redirectUrl = req.getContextPath() + "/apuesta?action=seleccionarEvento";
            if (req.getParameter("idEvento") != null) redirectUrl += "&idEvento=" + req.getParameter("idEvento");
            resp.sendRedirect(redirectUrl);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            // En caso de error inesperado, redirigir al index
            resp.sendRedirect(req.getContextPath() + "/jsp/index.jsp");
        } finally {
            em.close();
        }
    }
}