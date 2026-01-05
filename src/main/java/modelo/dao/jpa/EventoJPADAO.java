package modelo.dao.jpa;

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
    public Evento consultarDetallesEvento(int id) {
        Evento ev = em.find(Evento.class, id);
        return ev;
    }
}