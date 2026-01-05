package controlador;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.dao.JPAUtil;
import modelo.entidades.Evento;
import modelo.entidades.Pronostico;
import modelo.entidades.TipoCategoria;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@WebServlet("/initTestData")
public class InitTestDataController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = null;
        try {
            // Verificar si ya existen eventos
            TypedQuery<Long> q = em.createQuery("SELECT COUNT(e) FROM Evento e", Long.class);
            Long count = q.getSingleResult();
            if (count != null && count > 0) {
                resp.sendRedirect(req.getContextPath() + "/jsp/index.jsp");
                return;
            }

            tx = em.getTransaction();
            tx.begin();

            Evento ev = new Evento();
            ev.setNombre("Partido Amistoso: Selección A vs Selección B");
            ev.setFecha(LocalDateTime.now().plusDays(1));
            ev.setCategoria(TipoCategoria.DEPORTE);
            ev.setDescripcion("Encuentro amistoso entre Selección A y Selección B. No te lo pierdas.");

            em.persist(ev);
            // Asegurarnos de que el ID se genere antes de crear pronósticos que referencien id_evento
            em.flush();

            Pronostico p1 = new Pronostico();
            p1.setDescripcion("Gana Selección A");
            p1.setCuotaActual(1.85);
            p1.setId_evento(ev.getId());
            em.persist(p1);

            Pronostico p2 = new Pronostico();
            p2.setDescripcion("Gana Selección B");
            p2.setCuotaActual(2.10);
            p2.setId_evento(ev.getId());
            em.persist(p2);

            Pronostico p3 = new Pronostico();
            p3.setDescripcion("Empate");
            p3.setCuotaActual(3.25);
            p3.setId_evento(ev.getId());
            em.persist(p3);

            tx.commit();

            resp.sendRedirect(req.getContextPath() + "/jsp/index.jsp");
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creando datos de prueba: " + e.getMessage());
        } finally {
            em.close();
        }
    }
}
