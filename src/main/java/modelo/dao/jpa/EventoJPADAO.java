package modelo.dao.jpa;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import modelo.dao.EventoDAO;
import modelo.entidades.Evento;
import modelo.entidades.Pronostico;
import modelo.entidades.TipoCategoria;

public class EventoJPADAO implements EventoDAO {
    private final EntityManager em;

    public EventoJPADAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Evento> obtenerTodosLosEventos() {
        TypedQuery<Evento> q = em.createQuery("SELECT e FROM Evento e", Evento.class);
        return q.getResultList();
    }

    @Override
    public Evento consultarDetallesEvento(int id) {
    	PronosticoJPADAO pronosticosDAO = new PronosticoJPADAO(em);
    	List<Pronostico> pronosticos = pronosticosDAO.obtenerPronosticosPorEvento(em.find(Evento.class, id));
    	Evento evento = em.find(Evento.class, id);
    	evento.setPronosticos(pronosticos);
        return evento;
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
        if (evento == null) {
            return false;
        }

        boolean datosValidos = validarDatos(
            evento.getNombre(),
            evento.getDescripcion(),
            evento.getFecha(),
            evento.getCategoria()
        );

        if (!datosValidos) {
            System.out.println("Validación fallida: Datos incorrectos o fecha pasada.");
            return false;
        }
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            if(!evento.isEstado()) evento.setEstado(true);
            em.persist(evento);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
    
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

    @Override
    public boolean actualizarEvento(Evento evento) {
        if (evento == null) {
            return false;
        }

        boolean datosValidos = validarDatos(
            evento.getNombre(),
            evento.getDescripcion(),
            evento.getFecha(),
            evento.getCategoria()
        );

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