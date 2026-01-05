package controlador;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.dao.JPAUtil;
import modelo.entidades.Evento;

import java.io.IOException;
import java.util.List;

@WebServlet("/events")
public class EventsController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<Evento> q = em.createQuery("SELECT e FROM Evento e ORDER BY e.fecha", Evento.class);
            List<Evento> eventos = q.getResultList();
            req.setAttribute("eventos", eventos);
            RequestDispatcher rd = req.getRequestDispatcher("/jsp/index.jsp");
            rd.forward(req, resp);
        } finally {
            em.close();
        }
    }
}
