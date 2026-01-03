package modelo.dao.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import modelo.dao.IMovimientoDAO;
import modelo.entidades.Movimiento;

public class MovimientoJPADAO implements IMovimientoDAO {

    private final EntityManager em;

    public MovimientoJPADAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public boolean crearMovimiento(Movimiento movimiento) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(movimiento);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
}