package controlador;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.dao.JPAUtil;
import modelo.dao.jpa.EventoJPADAO;
import modelo.entidades.Evento;

import jakarta.persistence.EntityManager;
import java.io.IOException;
import java.util.List;

@WebServlet("/gestionarEventos")
public class GestionarEventosController extends HttpServlet {

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
                this.entrar(req, resp);
                break;
            default:
                this.entrar(req, resp);
                break;
        }
    }

    private void entrar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            EventoJPADAO eventoDAO = new EventoJPADAO(em);
            List<Evento> listaEventos = eventoDAO.obtenerTodosLosEventos();
            req.setAttribute("listaEventos", listaEventos);

            req.getRequestDispatcher("/jsp/adminHome.jsp").forward(req, resp);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }
}
