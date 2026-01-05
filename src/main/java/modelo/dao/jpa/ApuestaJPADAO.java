package modelo.dao.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import modelo.dao.ApuestaDAO;
import modelo.entidades.Apuesta;
import modelo.entidades.Pronostico;
import modelo.entidades.UsuarioRegistrado;
import modelo.entidades.EstadoApuesta;
import modelo.entidades.Billetera;
import modelo.entidades.Movimiento;
import modelo.entidades.TipoMovimiento;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

public class ApuestaJPADAO implements ApuestaDAO {
    private final EntityManager em;

    public ApuestaJPADAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Apuesta> obtenerApuestas() {
        TypedQuery<Apuesta> q = em.createQuery("SELECT a FROM Apuesta a", Apuesta.class);
        return q.getResultList();
    }

    @Override
    public List<Apuesta> filtrar() {
        // Implementación placeholder: retorna todas
        return obtenerApuestas();
    }

    @Override
    public void apostar(double monto, Pronostico pronostico, UsuarioRegistrado usuario) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // Obtener y verificar billetera dentro de la misma transacción
            TypedQuery<Billetera> q = em.createQuery("SELECT b FROM Billetera b WHERE b.usuario.id = :uid", Billetera.class);
            q.setParameter("uid", usuario.getId());
            Billetera billetera = q.getSingleResult();

            if (billetera == null || billetera.getSaldo() < monto) {
                tx.rollback();
                throw new IllegalStateException("Fondos insuficientes");
            }

            // Descontar el monto
            billetera.setSaldo(billetera.getSaldo() - monto);
            em.merge(billetera);

            // Crear la apuesta
            Apuesta a = new Apuesta();
            a.setIdUsuario(usuario.getId());
            if (pronostico != null) {
                a.setIdEvento(pronostico.getId_evento());
                a.setCuotaRegistrada(pronostico.getCuotaActual());
                a.setIdPronostico(pronostico.getId());
            }
            a.setMonto(monto);
            a.setEstado(EstadoApuesta.PENDIENTE);
            a.setFecha(LocalDateTime.now());

            em.persist(a);

            // Registrar movimiento de tipo APUESTA
            Movimiento mov = new Movimiento();
            mov.setUsuario(usuario);
            mov.setTipo(TipoMovimiento.APUESTA);
            mov.setMonto(monto);
            mov.setFecha(LocalDate.now());
            em.persist(mov);

            tx.commit();
        } catch (IllegalStateException ise) {
            if (tx.isActive()) tx.rollback();
            throw ise;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            throw e;
        }
    }
}