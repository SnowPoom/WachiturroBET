package controlador;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.dao.JPAUtil;
import modelo.entidades.Administrador;

import java.io.IOException;

@WebServlet("/adminLogin")
public class AdminLoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = null;
        try {
            HttpSession session = req.getSession(true);
            final String adminEmail = "admin@example.com";

            TypedQuery<Administrador> q = em.createQuery("SELECT a FROM Administrador a WHERE a.correo = :correo", Administrador.class);
            q.setParameter("correo", adminEmail);
            Administrador admin = null;
            try {
                admin = q.getSingleResult();
            } catch (NoResultException nre) {
                // Crear admin si no existe
                try {
                    tx = em.getTransaction();
                    tx.begin();
                    admin = new Administrador();
                    admin.setNombre("Admin");
                    admin.setApellido("User");
                    admin.setCorreo(adminEmail);
                    admin.setClave("admin");
                    em.persist(admin);
                    em.flush();
                    tx.commit();
                } catch (Exception ex) {
                    if (tx != null && tx.isActive()) tx.rollback();
                    throw new ServletException("Error creando administrador: " + ex.getMessage(), ex);
                }
            }

            // Asegurar entidad manejada
            Administrador managed = em.find(Administrador.class, admin.getId());
            session.setAttribute("currentUser", managed);
            session.setAttribute("currentUserId", managed.getId());

            // Redirigir a la página de gestión de eventos
            resp.sendRedirect(req.getContextPath() + "/gestionarEventos");
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }
}
