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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@WebServlet("/ApuestaController")
public class ApuestaController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ruteador(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    		ruteador(req, resp);
    }

    private void ruteador(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getParameter("ruta");
		if ("seleccionarEvento".equalsIgnoreCase(action)) {
			seleccionarEvento(req, resp);
		} else if ("ingresarMonto".equalsIgnoreCase(action)) {
			registrarMonto(req, resp);
		} else {
			resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
		}
	}
    
    private void seleccionarEvento(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
     String idStr = req.getParameter("id");
        EntityManager em = JPAUtil.getEntityManager();

        int id = Integer.parseInt(idStr);
        EventoJPADAO eventoDAO = new EventoJPADAO(em);
        Evento evento = eventoDAO.consultarDetallesEvento(id);
        
        req.setAttribute("eventoDetalle", evento);
        req.getRequestDispatcher("/jsp/detalleEvento.jsp").forward(req, resp);
    }

    private void registrarMonto(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
            usuario = em.find(UsuarioRegistrado.class, usuario.getId());

            String montoStr = req.getParameter("monto");
            String idPronosticoStr = req.getParameter("idPronostico");
            String idEventoStr = req.getParameter("idEvento"); 

            // Si se proporciona idEvento, verificar que el evento exista y esté activo
            Evento evento = null;
            if (idEventoStr != null) {
                try {
                    int idEvento = Integer.parseInt(idEventoStr);
                    evento = em.find(Evento.class, idEvento);
                    if (evento != null && !evento.isEstado()) {
                        // Evento finalizado: no permitir apuesta
                        session.setAttribute("flash_status", "ERROR");
                        session.setAttribute("flash_operacion", "APOSTAR");
                        session.setAttribute("flash_message", "mostrarEventoFinalizado");

                        String redirectUrl = req.getContextPath() + "/ApuestaController?ruta=seleccionarEvento";
                        redirectUrl += "&id=" + idEventoStr;
                        resp.sendRedirect(redirectUrl);
                        return;
                    }
                } catch (NumberFormatException ignored) {
                    evento = null;
                }
            }

            Double monto = null;
            try {
                if (montoStr != null) {
                    montoStr = montoStr.trim().replace(',', '.');
                    if (!montoStr.matches("^\\d+(\\.\\d{1,2})?$") ) throw new NumberFormatException();
                    BigDecimal bd = new BigDecimal(montoStr);
                    bd = bd.setScale(2, RoundingMode.HALF_UP);
                    monto = bd.doubleValue();
                }
            } catch (Exception e) { monto = null; }
            
           
            if (!esMontoValido(monto)) {
                session.setAttribute("flash_status", "ERROR");
                session.setAttribute("flash_operacion", "APOSTAR");
                // Nuevo: indicar mensaje específico para monto inválido
                session.setAttribute("flash_message", "mostrarMontoInvalido");
                
                String redirectUrl = req.getContextPath() + "/ApuestaController?ruta=seleccionarEvento";
                if (idEventoStr != null) redirectUrl += "&id=" + idEventoStr; 
                resp.sendRedirect(redirectUrl);
                return;
            }

            BilleteraJPADAO billeteraDAO = new BilleteraJPADAO(em);
            boolean tieneFondos = billeteraDAO.existenFondosValidos(usuario, monto);
            if (!tieneFondos) {
                session.setAttribute("flash_status", "ERROR");
                session.setAttribute("flash_operacion", "APOSTAR");
                session.setAttribute("flash_message", "mostrarFondosInsuficientes");
                
                
                String redirectUrl = req.getContextPath() + "/ApuestaController?ruta=seleccionarEvento";
                if (idEventoStr != null) redirectUrl += "&id=" + idEventoStr;
                resp.sendRedirect(redirectUrl);
                return;
            }

            Pronostico pronostico = null;
            if (idPronosticoStr != null) {
                try {
                    int pid = Integer.parseInt(idPronosticoStr);
                    pronostico = em.find(Pronostico.class, pid);
                } catch (NumberFormatException ignored) {}
            }

            ApuestaJPADAO apuestaDAO = new ApuestaJPADAO(em);
            apuestaDAO.guardarMovimiento(monto, pronostico, usuario);

            try {
                TypedQuery<Double> q = em.createQuery("SELECT b.saldo FROM Billetera b WHERE b.usuario.id = :uid", Double.class);
                q.setParameter("uid", usuario.getId());
                Double saldo = q.getSingleResult();
                session.setAttribute("currentUserSaldo", saldo != null ? saldo : 0.0);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            session.setAttribute("flash_status", "OK");
            session.setAttribute("flash_operacion", "APOSTAR");
            session.setAttribute("flash_message", "Apuesta realizada correctamente");
            
            
            String redirectUrl = req.getContextPath() + "/ApuestaController?ruta=seleccionarEvento";
            if (idEventoStr != null) redirectUrl += "&id=" + idEventoStr;
            resp.sendRedirect(redirectUrl);

        } catch (IllegalStateException ise) {
            HttpSession session = req.getSession();
            session.setAttribute("flash_status", "ERROR");
            session.setAttribute("flash_operacion", "APOSTAR");
            session.setAttribute("flash_message", "mostrarFondosInsuficientes");
            
            
            String redirectUrl = req.getContextPath() + "/ApuestaController?ruta=seleccionarEvento";
            if (req.getParameter("idEvento") != null) redirectUrl += "&id=" + req.getParameter("idEvento");
            resp.sendRedirect(redirectUrl);
            return;
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect(req.getContextPath() + "/jsp/index.jsp");
        } finally {
            em.close();
        }
        
  
    }
    
    public boolean esMontoValido(Double monto) {
    	return monto != null && monto > 0;
    }
}
