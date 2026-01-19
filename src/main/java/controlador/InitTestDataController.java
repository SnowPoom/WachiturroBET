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

@WebServlet("/initTestData")
public class InitTestDataController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = null;
        try {
            // --- LÓGICA NUEVA: CAMBIO DE SESIÓN ---
            // Si recibimos el parámetro forceUser, limpiamos la sesión (sacamos al Admin)
            if ("true".equals(req.getParameter("forceUser"))) {
                HttpSession session = req.getSession(false);
                if (session != null) {
                    session.removeAttribute("currentUser");
                    session.removeAttribute("currentUserId");
                }
            }
            // --------------------------------------

            // 1. Verificar si ya existen eventos
            TypedQuery<Long> q = em.createQuery("SELECT COUNT(e) FROM Evento e", Long.class);
            Long count = q.getSingleResult();
            
            // Si ya hay datos, aseguramos el usuario y redirigimos
            if (count != null && count > 0) {
                ensureTestUser(req, em);
                // Redirigimos al Home del Usuario
                resp.sendRedirect(req.getContextPath() + "/index.jsp"); 
                return;
            }

            tx = em.getTransaction();
            tx.begin();

            System.out.println("--- INICIANDO CREACIÓN DE DATOS DE PRUEBA ---");

            // --- EVENTO 1: FÚTBOL ---
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

            // --- EVENTO 2: E-SPORTS ---
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

            // --- EVENTO 3: TENIS ---
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

            tx.commit();
            System.out.println("--- DATOS CREADOS CORRECTAMENTE ---");

            // Aseguramos usuario de prueba
            ensureTestUser(req, em);

            // Redirigir al Home del Usuario
            resp.sendRedirect(req.getContextPath() + "/index.jsp");
            
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error creando datos: " + e.getMessage());
        } finally {
            if (em != null && em.isOpen()) em.close();
        }
    }

    private void crearEventoCompleto(EntityManager em, String nombre, String desc, LocalDateTime fecha, TipoCategoria cat, Object[][] datosPronosticos) {
        Evento ev = new Evento();
        ev.setNombre(nombre);
        ev.setDescripcion(desc);
        ev.setFecha(fecha);
        ev.setCategoria(cat);
        // Por defecto estado = true (Abierto)
        ev.setEstado(true); 
        em.persist(ev);
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

    public static void ensureTestUser(HttpServletRequest req, EntityManager em) {
        HttpSession session = req.getSession(true);
        // Si ya hay alguien logueado (y no lo borramos antes), no hacemos nada
        if (session.getAttribute("currentUser") != null) return;

        final String testEmail = "test@example.com";
        EntityTransaction tx = null;
        UsuarioRegistrado usuario = null;
        try {
            TypedQuery<UsuarioRegistrado> q = em.createQuery("SELECT u FROM UsuarioRegistrado u WHERE u.correo = :correo", UsuarioRegistrado.class);
            q.setParameter("correo", testEmail);
            try {
                usuario = q.getSingleResult();
            } catch (NoResultException nre) {
                tx = em.getTransaction();
                tx.begin();
                usuario = new UsuarioRegistrado();
                usuario.setNombre("Usuario");
                usuario.setApellido("Prueba");
                usuario.setCorreo(testEmail);
                usuario.setClave("password");
                em.persist(usuario);

                Billetera billetera = new Billetera();
                billetera.setSaldo(5000.0); 
                billetera.setUsuario(usuario);
                em.persist(billetera);
                em.flush();
                tx.commit();
            }

            if (usuario != null) {
                // Refrescamos la entidad para evitar problemas de detached
                UsuarioRegistrado managedUser = em.find(UsuarioRegistrado.class, usuario.getId());
                actualizarSaldoEnSesion(session, managedUser, em);
                session.setAttribute("currentUser", managedUser);
                session.setAttribute("currentUserId", managedUser.getId());
            }
        } catch (Exception e) {
            if (tx != null && tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }

    public static void actualizarSaldoEnSesion(HttpSession session, UsuarioRegistrado usuario, EntityManager em) {
        if (session != null) {
            try {
                em.getEntityManagerFactory().getCache().evictAll();
                TypedQuery<Double> q = em.createQuery("SELECT b.saldo FROM Billetera b WHERE b.usuario.id = :uid", Double.class);
                q.setParameter("uid", usuario.getId());
                Double saldo = q.getSingleResult();
                session.setAttribute("currentUserSaldo", saldo != null ? saldo : 0.0);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}