package modelo.dao.jpa;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import modelo.dao.EventoDAO;
import modelo.entidades.Evento;
import modelo.entidades.TipoCategoria;

public class EventoJPADAO implements EventoDAO {
    private final EntityManager em;

    public EventoJPADAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public String obtenerNombreEvento() {
        TypedQuery<String> q = em.createQuery("SELECT e.nombre FROM Evento e", String.class);
        q.setMaxResults(1);
        return q.getResultList().stream().findFirst().orElse("Evento desconocido");
    }

    @Override
    public List<Evento> obtenerTodosLosEventos() {
        TypedQuery<Evento> q = em.createQuery("SELECT e FROM Evento e", Evento.class);
        return q.getResultList();
    }

    @Override
    public Evento consultarDetallesEvento(int id) {
        return em.find(Evento.class, id);
    }
    
    @Override
    public List<Evento> obtenerEventosDisponibles() {
        String jpql = "SELECT e FROM Evento e WHERE e.estado = true ORDER BY e.fecha ASC";
        TypedQuery<Evento> query = em.createQuery(jpql, Evento.class);
        return query.getResultList();
    }
    
    public boolean validarDatos(String nombre, String descripcion, LocalDateTime fecha, TipoCategoria categoria) {
        if (nombre == null || nombre.trim().isEmpty()) return false;
        if (fecha == null) return false;
        if (categoria == null) return false;
        if (fecha.isBefore(LocalDateTime.now())) return false;
        return true;
    }

    @Override
    public boolean crearEvento(Evento evento) {
    	// 1. Verificación de seguridad básica (evitar NullPointerException)
        if (evento == null) {
            return false;
        }

        // 2. Aplicamos tu método de validación extrayendo los datos del objeto
        boolean datosValidos = validarDatos(
            evento.getNombre(),
            evento.getDescripcion(),
            evento.getFecha(),
            evento.getCategoria() // Asumo que tienes un getter para la categoría
        );

        // Si la validación falla, retornamos false inmediatamente sin tocar la BD
        if (!datosValidos) {
            System.out.println("Validación fallida: Datos incorrectos o fecha pasada.");
            return false;
        }
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if(!evento.isEstado()) evento.setEstado(true); // Por defecto activo
            em.persist(evento);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
    
    // Método necesario para finalizar evento (lógica de negocio específica)
    public boolean finalizarEvento(Evento evento) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            evento.setEstado(false);
            em.merge(evento);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    // --- NUEVOS MÉTODOS PARA EL REST CRUD ---

    @Override
    public boolean actualizarEvento(Evento evento) {
    	// 1. Verificación de seguridad básica (evitar NullPointerException)
        if (evento == null) {
            return false;
        }

        // 2. Aplicamos tu método de validación extrayendo los datos del objeto
        boolean datosValidos = validarDatos(
            evento.getNombre(),
            evento.getDescripcion(),
            evento.getFecha(),
            evento.getCategoria() // Asumo que tienes un getter para la categoría
        );

        // Si la validación falla, retornamos false inmediatamente sin tocar la BD
        if (!datosValidos) {
            return false;
        }
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.merge(evento);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean eliminarEvento(int id) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Evento evento = em.find(Evento.class, id);
            if (evento != null) {
                em.remove(evento);
                tx.commit();
                return true;
            }
            tx.rollback();
            return false;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Evento> buscarEventosPorNombre(String texto) {
        String jpql = "SELECT e FROM Evento e WHERE LOWER(e.nombre) LIKE LOWER(:texto) ORDER BY e.fecha ASC";
        TypedQuery<Evento> query = em.createQuery(jpql, Evento.class);
        query.setParameter("texto", "%" + texto + "%");
        return query.getResultList();
    }
}