package controlador;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.NoResultException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.dao.JPAUtil;
import modelo.entidades.Evento;
import modelo.entidades.Pronostico;
import modelo.entidades.TipoCategoria;
import modelo.entidades.UsuarioRegistrado;
import modelo.entidades.Billetera;

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
                // Asegurarnos también de que el usuario de prueba esté en sesión
                ensureTestUser(req, em);
                // Ir a /events en lugar de la vista JSP
                resp.sendRedirect(req.getContextPath() + "/events");
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
            // Asociar correctamente el evento al pronóstico
            p1.setEvento(ev);
            em.persist(p1);

            Pronostico p2 = new Pronostico();
            p2.setDescripcion("Gana Selección B");
            p2.setCuotaActual(2.10);
            // Asociar correctamente el evento al pronóstico
            p2.setEvento(ev);
            em.persist(p2);

            Pronostico p3 = new Pronostico();
            p3.setDescripcion("Empate");
            p3.setCuotaActual(3.25);
            // Asociar correctamente el evento al pronóstico
            p3.setEvento(ev);
            em.persist(p3);

            tx.commit();

            // Aseguramos también el usuario de prueba en sesión después de crear los datos
            ensureTestUser(req, em);

            // Ir a /events
            resp.sendRedirect(req.getContextPath() + "/events");
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creando datos de prueba: " + e.getMessage());
        } finally {
            em.close();
        }
    }

    // Moved helper: ensureTestUser (public static so it can be reused by other controllers)
    public static void ensureTestUser(HttpServletRequest req, EntityManager em) {
        HttpSession session = req.getSession(true);
        if (session.getAttribute("currentUser") != null) {
            System.out.println("[InitTestDataController] ensureTestUser: currentUser ya en sesión");
            return;
        }

        final String testEmail = "test@example.com";
        EntityTransaction tx = null;
        UsuarioRegistrado usuario = null;
        try {
            TypedQuery<UsuarioRegistrado> q = em.createQuery("SELECT u FROM UsuarioRegistrado u WHERE u.correo = :correo", UsuarioRegistrado.class);
            q.setParameter("correo", testEmail);
            try {
                usuario = q.getSingleResult();
                System.out.println("[InitTestDataController] ensureTestUser: usuario existente id=" + usuario.getId());
            } catch (NoResultException nre) {
                // Crear usuario y billetera de prueba
                tx = em.getTransaction();
                tx.begin();
                usuario = new UsuarioRegistrado();
                usuario.setNombre("Usuario");
                usuario.setApellido("Prueba");
                usuario.setCorreo(testEmail);
                usuario.setClave("password");
                em.persist(usuario);

                Billetera billetera = new Billetera();
                billetera.setSaldo(100.0);
                billetera.setUsuario(usuario);
                em.persist(billetera);

                // Forzar asignación de IDs antes del commit para mayor seguridad
                em.flush();
                System.out.println("[InitTestDataController] ensureTestUser: usuario creado provisional id=" + usuario.getId());

                tx.commit();
                System.out.println("[InitTestDataController] ensureTestUser: commit de creación realizado");
            }

            if (usuario != null) {
                // Reload managed entity from this EntityManager to avoid detached instances in session
                UsuarioRegistrado managedUser = em.find(UsuarioRegistrado.class, usuario.getId());
                if (managedUser == null) {
                    // If still null (very unlikely), try to refresh by querying
                    TypedQuery<UsuarioRegistrado> q2 = em.createQuery("SELECT u FROM UsuarioRegistrado u WHERE u.correo = :correo", UsuarioRegistrado.class);
                    q2.setParameter("correo", testEmail);
                    try { managedUser = q2.getSingleResult(); } catch (Exception ex) { managedUser = usuario; }
                }

                // Update saldo en sesión and set user in session using the same HttpSession
                actualizarSaldoEnSesion(session, managedUser, em);
                session.setAttribute("currentUser", managedUser);
                // Also store the id and timestamp to help debugging and to avoid relying solely on entity serialization
                session.setAttribute("currentUserId", managedUser.getId());
                session.setAttribute("currentUserSetAt", System.currentTimeMillis());

                System.out.println("[InitTestDataController] ensureTestUser: usuario en sesión con id=" + managedUser.getId());
                System.out.println("[InitTestDataController] ensureTestUser: session attributes -> currentUser=" + (session.getAttribute("currentUser")!=null) + ", currentUserId=" + session.getAttribute("currentUserId"));
            }
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    // Moved helper: actualizarSaldoEnSesion (now accepts HttpSession explicitly)
    public static void actualizarSaldoEnSesion(HttpSession session, UsuarioRegistrado usuario, EntityManager em) {
        if (session != null) {
            try {
                // Forzamos limpiar caché antes de consultar saldo
                em.getEntityManagerFactory().getCache().evictAll();

                TypedQuery<Double> q = em.createQuery("SELECT b.saldo FROM Billetera b WHERE b.usuario.id = :uid", Double.class);
                q.setParameter("uid", usuario.getId());
                Double saldo = q.getSingleResult();
                session.setAttribute("currentUserSaldo", saldo != null ? saldo : 0.0);
                System.out.println("[InitTestDataController] actualizarSaldoEnSesion: saldo=" + saldo);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}