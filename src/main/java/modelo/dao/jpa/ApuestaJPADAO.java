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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ApuestaJPADAO implements ApuestaDAO {
    private final EntityManager em;

    public ApuestaJPADAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public List<Apuesta> obtenerApuestas() {
        TypedQuery<Apuesta> q = em.createQuery("SELECT a FROM Apuesta a ORDER BY a.fecha DESC", Apuesta.class);
        return q.getResultList();
    }

    @Override
    public List<Apuesta> filtrar(LocalDateTime fechaInicio, LocalDateTime fechaFin, String estado) {
        StringBuilder sb = new StringBuilder("SELECT a FROM Apuesta a WHERE 1=1");
        if (fechaInicio != null) sb.append(" AND a.fecha >= :fInicio");
        if (fechaFin != null) sb.append(" AND a.fecha <= :fFin");
        if (estado != null && !estado.trim().isEmpty()) sb.append(" AND a.estado = :estado");
        sb.append(" ORDER BY a.fecha DESC");

        TypedQuery<Apuesta> q = em.createQuery(sb.toString(), Apuesta.class);
        if (fechaInicio != null) q.setParameter("fInicio", fechaInicio);
        if (fechaFin != null) q.setParameter("fFin", fechaFin);
        if (estado != null && !estado.trim().isEmpty()) {
            try {
                EstadoApuesta e = EstadoApuesta.valueOf(estado);
                q.setParameter("estado", e);
            } catch (IllegalArgumentException iae) {
                // estado inválido -> devolver vacío
                return new ArrayList<>();
            }
        }
        return q.getResultList();
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

            // Crear la apuesta (Apuesta ahora extiende Movimiento)
            Apuesta a = new Apuesta();
            a.setIdUsuario(usuario.getId());
            if (pronostico != null) {
                a.setPronostico(pronostico);
                a.setCuotaRegistrada(pronostico.getCuotaActual());
            }
            a.setMonto(monto);
            a.setEstado(EstadoApuesta.PENDIENTE);
            a.setFecha(LocalDateTime.now());

            em.persist(a);

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