package controlador;

import modelo.dao.JPAUtil;
import modelo.dao.jpa.PronosticoJPADAO;
import modelo.entidades.Evento;
import modelo.entidades.Pronostico;

import jakarta.persistence.EntityManager;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/gestionarPronosticos")
public class GestionarPronosticosController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.ruteador(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.ruteador(req, resp);
    }

    private void ruteador(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ruta = (req.getParameter("ruta") != null) ? req.getParameter("ruta") : "entrar";

        switch (ruta) {
            case "entrar":
                // Mapeo exacto al mensaje 1 del diagrama: entrar(evento)
                this.entrar(req, resp);
                break;
            case "guardar":
                // Mapeo al mensaje 3 del diagrama: ingresarDatos(...)
                this.ingresarDatos(req, resp);
                break;
            default:
                this.entrar(req, resp);
                break;
        }
    }

    // --- MÉTODOS DEL DIAGRAMA ---

    // 1: entrar(evento)
    private void entrar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Limpieza de mensajes flash
        if ("true".equals(req.getParameter("reset"))) {
            HttpSession session = req.getSession(false);
            if (session != null) {
                session.removeAttribute("flash_status");
                session.removeAttribute("flash_message");
            }
        }

        String idEventoStr = req.getParameter("idEvento");
        if (idEventoStr == null || idEventoStr.isEmpty()) {
            resp.sendRedirect(req.getContextPath() + "/gestionarEventos");
            return;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            int idEvento = Integer.parseInt(idEventoStr);
            Evento evento = em.find(Evento.class, idEvento);
            
            if (evento == null) {
                resp.sendRedirect(req.getContextPath() + "/gestionarEventos");
                return;
            }

            PronosticoJPADAO pronosticoDAO = new PronosticoJPADAO(em);
            List<Pronostico> lista = pronosticoDAO.obtenerPronosticosPorEvento(evento);

            // Pasamos el OBJETO COMPLETO evento al JSP para poder verificar su estado (abierto/cerrado)
            req.setAttribute("evento", evento); 
            req.setAttribute("nombreEvento", evento.getNombre());
            req.setAttribute("listaPronosticos", lista);
            req.setAttribute("idEvento", idEvento); 

            RequestDispatcher rd = req.getRequestDispatcher("/jsp/pronosticos.jsp");
            rd.forward(req, resp);

        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/gestionarEventos");
        } finally {
            em.close();
        }
    }

    // 3: ingresarDatos(...)
    private void ingresarDatos(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        EntityManager em = JPAUtil.getEntityManager();
        String idEventoStr = req.getParameter("idEvento");
        
        try {
            int idEvento = Integer.parseInt(idEventoStr);
            Evento evento = em.find(Evento.class, idEvento);

            // --- VALIDACIÓN DE ESTADO CERRADO ---
            if (evento == null || !evento.isEstado()) { // Si estado es false (Cerrado)
                setFlash(req, "ERROR", "No se pueden agregar pronósticos a un evento finalizado.");
                resp.sendRedirect(req.getContextPath() + "/gestionarPronosticos?idEvento=" + idEventoStr);
                return;
            }
            // ------------------------------------

            String descripcion = req.getParameter("descripcion");
            String cuotaStr = req.getParameter("cuota");
            double cuota = Double.parseDouble(cuotaStr);

            PronosticoJPADAO dao = new PronosticoJPADAO(em);

            if (dao.verificarDatos(descripcion, cuota)) {
                Pronostico nuevo = new Pronostico();
                nuevo.setDescripcion(descripcion);
                nuevo.setCuotaActual(cuota);
                nuevo.setEvento(evento);

                boolean creado = dao.crearPronostico(nuevo);

                if (creado) {
                    setFlash(req, "OK", "Pronóstico agregado correctamente.");
                } else {
                    setFlash(req, "ERROR", "Error al guardar en base de datos.");
                }
            } else {
                setFlash(req, "ERROR", "Datos inválidos: Revise descripción y cuota (> 1.0).");
            }

        } catch (Exception e) {
            setFlash(req, "ERROR", "Error inesperado: " + e.getMessage());
        } finally {
            em.close();
            resp.sendRedirect(req.getContextPath() + "/gestionarPronosticos?idEvento=" + idEventoStr);
        }
    }

    private void setFlash(HttpServletRequest req, String status, String message) {
        HttpSession session = req.getSession();
        session.setAttribute("flash_status", status);
        session.setAttribute("flash_message", message);
    }
}