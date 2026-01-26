package modelo.dao.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
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
        // Ordenamos por id ascendente para asegurar orden consistente en la UI
        TypedQuery<Pronostico> q = em.createQuery("SELECT p FROM Pronostico p WHERE p.evento.id = :eid ORDER BY p.id ASC", Pronostico.class);
        q.setParameter("eid", evento.getId());
        return q.getResultList();
    }

    @Override
    public String obtenerDescripcion(int idPronostico) {
        Pronostico p = em.find(Pronostico.class, idPronostico);
        return p != null ? p.getDescripcion() : null;
    }
    
 // --- MÉTODOS DEL DIAGRAMA DE SECUENCIA ---

  
 // 3.1: verificarDatos (Validación de negocio)
    public boolean verificarDatos(String descripcion, double cuota) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            return false;
        }
        // La cuota debe ser mayor a 1.0 para tener sentido en apuestas
        if (cuota <= 1.0) {
            return false;
        }
        return true;
    }

    // 4.1: crearPronostico
    public boolean crearPronostico(Pronostico pronostico) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Estado inicial: no es ganador
            pronostico.setEsGanador(false);
            em.persist(pronostico);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
 // 3.2: obtenerPronosticoGanador (y marcarlo)
    public Pronostico marcarComoGanador(int idPronostico) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Pronostico p = em.find(Pronostico.class, idPronostico);
            if (p != null) {
                p.setEsGanador(true);
                em.merge(p);
            }
            tx.commit();
            return p;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            return null;
        }
    }
}