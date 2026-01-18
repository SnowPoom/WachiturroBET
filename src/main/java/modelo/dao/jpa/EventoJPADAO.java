package modelo.dao.jpa;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import modelo.dao.EventoDAO;
import modelo.entidades.Evento;

public class EventoJPADAO implements EventoDAO {
    private final EntityManager em;

    public EventoJPADAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public String obtenerNombreEvento() {
        // Ejemplo simple: retorna el primer nombre de evento encontrado
        TypedQuery<String> q = em.createQuery("SELECT e.nombre FROM Evento e", String.class);
        q.setMaxResults(1);
        return q.getResultList().stream().findFirst().orElse("Evento desconocido");
    }
    @Override
    public java.util.List<Evento> obtenerTodosLosEventos() {
        TypedQuery<Evento> q = em.createQuery("SELECT e FROM Evento e", Evento.class);
        return q.getResultList();
    }
    @Override
    public Evento consultarDetallesEvento(int id) {
        return em.find(Evento.class, id);
    }
    
    public List<Evento> obtenerEventosDisponibles() {
        // Esto hace que sea DINÁMICO. Trae todo lo que esté en la BD.
        // Si el admin agrega un evento hoy, mañana aparecerá aquí automáticamente.
        String jpql = "SELECT e FROM Evento e ORDER BY e.fecha ASC";
        TypedQuery<Evento> query = em.createQuery(jpql, Evento.class);
        return query.getResultList();
    }
    
    
}