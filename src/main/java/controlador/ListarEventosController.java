package controlador;

import jakarta.persistence.EntityManager;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.dao.JPAUtil;
import modelo.dao.jpa.EventoJPADAO;
import modelo.dao.jpa.PronosticoJPADAO;
import modelo.entidades.Evento;
import modelo.entidades.Pronostico;

import java.io.IOException;
import java.util.List;

// Usamos el nombre de la clase como URL para seguir el estándar de tu profesor
@WebServlet("/ListarEventosController")
public class ListarEventosController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public ListarEventosController() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.ruteador(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.ruteador(req, resp);
    }

    // --- EL CEREBRO (Router) ---
    private void ruteador(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Por defecto, la ruta es "entrar" (Ver lista de eventos)
        String ruta = (req.getParameter("ruta") == null) ? "entrar" : req.getParameter("ruta");

        switch (ruta) {
            case "entrar":
                this.entrar(req, resp);
                break;
            case "seleccionarEvento":
                this.seleccionarEvento(req, resp);
                break;
            default:
                this.entrar(req, resp);
                break;
        }
    }

    // --- MÉTODOS DEL DIAGRAMA DE SECUENCIA ---

    /**
     * Flecha 1: entrar()
     */
    private void entrar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            EventoJPADAO eventoDAO = new EventoJPADAO(em);

            // Flecha 1.1: obtenerEventosDisponibles()
            List<Evento> lista = eventoDAO.obtenerEventosDisponibles();
            
            // Flecha 1.1.1.1: mostrarListaEventos()
            this.mostrarListaEventos(req, resp, lista);

        } finally {
            if (em != null) em.close();
        }
    }

    /**
     * Flecha 2: seleccionarEvento(id)
     */
    private void seleccionarEvento(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Validamos que venga el ID
            String idStr = req.getParameter("id");
            if (idStr == null) {
                resp.sendRedirect("ListarEventosController?ruta=entrar");
                return;
            }
            int id = Integer.parseInt(idStr);

            EventoJPADAO eventoDAO = new EventoJPADAO(em);
            PronosticoJPADAO pronosticoDAO = new PronosticoJPADAO(em);

            // Flecha 2.1: consultarDetallesEvento()
            Evento evento = eventoDAO.consultarDetallesEvento(id);

            // Flecha 2.1.1: presentarEvento()
            if (evento != null) {
            	List<Pronostico> listaPronosticos = pronosticoDAO.obtenerPronosticosPorEvento(evento);
                this.presentarEvento(req, resp, evento, listaPronosticos);
            } else {
                resp.sendRedirect("ListarEventosController?ruta=entrar");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.sendRedirect("ListarEventosController?ruta=entrar");
        } finally {
            if (em != null) em.close();
        }
    }

    // --- MÉTODOS DE VISTA (Boundaries) ---

    /**
     * Flecha 1.1.1.1: mostrarListaEventos()
     */
    private void mostrarListaEventos(HttpServletRequest req, HttpServletResponse resp, List<Evento> lista) throws ServletException, IOException {
        req.setAttribute("eventos", lista); // "eventos" es como lo llamaba el jsp original
        RequestDispatcher rd = req.getRequestDispatcher("/jsp/index.jsp"); // O index.jsp si prefieres
        rd.forward(req, resp);
    }

    /**
     * Flecha 2.1.1: presentarEvento()
     */
    // Actualizamos este método para recibir la lista explícita
    private void presentarEvento(HttpServletRequest req, HttpServletResponse resp, Evento evento, List<Pronostico> pronosticos) throws ServletException, IOException {
        req.setAttribute("eventoDetalle", evento);
        req.setAttribute("pronosticos", pronosticos); // <--- La lista que trajo tu DAO
        
        RequestDispatcher rd = req.getRequestDispatcher("/jsp/detalleEvento.jsp");
        rd.forward(req, resp);
    }
    
}