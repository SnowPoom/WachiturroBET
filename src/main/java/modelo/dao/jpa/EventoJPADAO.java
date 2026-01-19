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
        String jpql = "SELECT e FROM Evento e ORDER BY e.fecha ASC";
        TypedQuery<Evento> query = em.createQuery(jpql, Evento.class);
        return query.getResultList();
    }
    
 // Paso 2.1: validarDatos
    public boolean validarDatos(String nombre, String descripcion, LocalDateTime fecha, TipoCategoria categoria) {
        // 1. Validar campos obligatorios vacíos
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        if (fecha == null) {
            return false;
        }
        if (categoria == null) {
            return false;
        }

        // 2. Regla de Negocio: No se pueden crear eventos en el pasado
        if (fecha.isBefore(LocalDateTime.now())) {
            return false;
        }

        return true;
    }

    // Paso 2.3: crearEvento
    public boolean crearEvento(Evento evento) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            // Aseguramos que el evento nazca "Abierto" (true) si no se ha seteado
            evento.setEstado(true);
            
            em.persist(evento);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
 // 3.1: finalizarEvento
    public boolean finalizarEvento(Evento evento) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            evento.setEstado(false); // false = Cerrado
            em.merge(evento);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
}