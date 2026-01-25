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
import modelo.entidades.Billetera;
import modelo.entidades.Evento;
import modelo.entidades.Pronostico;
import modelo.entidades.TipoCategoria;
import modelo.entidades.UsuarioRegistrado;

import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/initTestData")
public class InitTestDataController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = null;

        try {
            // 1. Limpieza de sesión si se solicita (para pruebas)
            if ("true".equals(req.getParameter("forceUser"))) {
                HttpSession session = req.getSession(false);
                if (session != null) {
                    session.invalidate(); // Mata la sesión completa
                }
            }

            // 2. Verificar si ya existen datos para no duplicar
            TypedQuery<Long> q = em.createQuery("SELECT COUNT(e) FROM Evento e", Long.class);
            Long count = q.getSingleResult();

            if (count != null && count > 0) {
                // Si ya hay eventos, solo aseguramos que los usuarios existan y redirigimos
                verificarYCrearUsuarios(em);
                resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
                return;
            }

            // 3. Crear datos iniciales
            tx = em.getTransaction();
            tx.begin();

            System.out.println("--- INICIANDO CREACIÓN DE DATOS DE PRUEBA ---");

            // --- EVENTOS ---
            crearEventosIniciales(em);

            tx.commit(); // Hacemos commit de los eventos primero

            // 4. Crear Usuarios (Manejamos su propia transacción dentro del método o reusamos)
            verificarYCrearUsuarios(em);

            System.out.println("--- DATOS CREADOS CORRECTAMENTE ---");

            // Redirigir al login
            resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");

        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creando datos: " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    // --- MÉTODOS AUXILIARES DE CREACIÓN DE ENTIDADES ---

    private void verificarYCrearUsuarios(EntityManager em) {
        // Creamos el Usuario Normal (Apostador)
        crearUsuarioRegistradoSiNoExiste(em, "user@test.com", "User", "Prueba", "1234");
        
        // Creamos el Administrador
        crearAdministradorSiNoExiste(em, "admin@test.com", "Admin", "Sistema", "1234");
    }

    private void crearUsuarioRegistradoSiNoExiste(EntityManager em, String correo, String nombre, String apellido, String clave) {
        EntityTransaction tx = em.getTransaction();
        try {
            // Verificar si existe
            TypedQuery<Long> q = em.createQuery("SELECT COUNT(u) FROM UsuarioRegistrado u WHERE u.correo = :correo", Long.class);
            q.setParameter("correo", correo);
            if (q.getSingleResult() > 0) return;

            // Si no existe, crear
            if (!tx.isActive()) tx.begin();

            UsuarioRegistrado u = new UsuarioRegistrado();
            u.setNombre(nombre);
            u.setApellido(apellido);
            u.setCorreo(correo);
            u.setClave(clave);
            em.persist(u);

            // Crear Billetera para el usuario registrado
            Billetera b = new Billetera();
            b.setSaldo(5000.0); // Saldo inicial de prueba
            b.setUsuario(u);
            em.persist(b);

            tx.commit();
            System.out.println("Usuario Registrado creado: " + correo);

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    private void crearAdministradorSiNoExiste(EntityManager em, String correo, String nombre, String apellido, String clave) {
        EntityTransaction tx = em.getTransaction();
        try {
            // Verificar si existe
            TypedQuery<Long> q = em.createQuery("SELECT COUNT(a) FROM Administrador a WHERE a.correo = :correo", Long.class);
            q.setParameter("correo", correo);
            if (q.getSingleResult() > 0) return;

            // Si no existe, crear
            if (!tx.isActive()) tx.begin();

            Administrador admin = new Administrador();
            admin.setNombre(nombre);
            admin.setApellido(apellido);
            admin.setCorreo(correo);
            admin.setClave(clave);
            // Nota: El administrador generalmente no lleva Billetera en este modelo
            em.persist(admin);

            tx.commit();
            System.out.println("Administrador creado: " + correo);

        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    private void crearEventosIniciales(EntityManager em) {
        // Evento 1: Fútbol
        crearEventoCompleto(em,
            "Real Madrid vs FC Barcelona",
            "La Liga - Jornada 32. El clásico español decisivo por el título.",
            LocalDateTime.now().plusDays(2),
            TipoCategoria.DEPORTES,
            new Object[][] {
                {"Gana Real Madrid", 2.15},
                {"Empate", 3.40},
                {"Gana Barcelona", 3.00}
            }
        );

        // Evento 2: E-Sports
        crearEventoCompleto(em,
            "T1 vs Gen.G - Worlds Final",
            "Gran Final del Campeonato Mundial de League of Legends 2026.",
            LocalDateTime.now().plusDays(5),
            TipoCategoria.ESPORTS,
            new Object[][] {
                {"Gana T1 (Faker)", 1.65},
                {"Gana Gen.G", 2.20}
            }
        );

        // Evento 3: Tenis
        crearEventoCompleto(em,
            "Alcaraz vs Sinner",
            "Final de Roland Garros. Duelo de la nueva generación.",
            LocalDateTime.now().plusDays(1),
            TipoCategoria.DEPORTES,
            new Object[][] {
                {"Gana Alcaraz", 1.85},
                {"Gana Sinner", 1.85},
                {"Más de 3.5 Sets", 1.40}
            }
        );
    }

    private void crearEventoCompleto(EntityManager em, String nombre, String desc, LocalDateTime fecha, TipoCategoria cat, Object[][] datosPronosticos) {
        Evento ev = new Evento();
        ev.setNombre(nombre);
        ev.setDescripcion(desc);
        ev.setFecha(fecha);
        ev.setCategoria(cat);
        ev.setEstado(true);
        em.persist(ev);
        // Hacemos flush para obtener el ID del evento antes de guardar pronósticos
        em.flush();

        for (Object[] datos : datosPronosticos) {
            String descripcionPronostico = (String) datos[0];
            Double cuota = (Double) datos[1];

            Pronostico p = new Pronostico();
            p.setDescripcion(descripcionPronostico);
            p.setCuotaActual(cuota);
            p.setEvento(ev);
            p.setEsGanador(false);
            em.persist(p);
        }
    }
}