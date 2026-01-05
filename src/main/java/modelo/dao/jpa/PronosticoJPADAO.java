package modelo.dao.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import modelo.dao.PronosticoDAO;
import modelo.entidades.Evento;
import modelo.entidades.Pronostico;

import java.util.List;

public class PronosticoJPADAO implements PronosticoDAO {
    private final EntityManager em;

    public PronosticoJPADAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Pronostico> obtenerPronosticosPorEvento(Evento evento) {
        // Implementación simple asumiendo que existe una entidad Pronostico mapeada
        TypedQuery<Pronostico> q = em.createQuery("SELECT p FROM Pronostico p WHERE p.evento.id = :eid", Pronostico.class);
        q.setParameter("eid", evento.getId());
        return q.getResultList();
    }

    @Override
    public String obtenerDescripcion(int idPronostico) {
        Pronostico p = em.find(Pronostico.class, idPronostico);
        return p != null ? p.getDescripcion() : null;
    }
}