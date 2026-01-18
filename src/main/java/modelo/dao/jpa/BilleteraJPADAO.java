package modelo.dao.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import modelo.dao.IBilleteraDAO;
import modelo.entidades.Billetera;
import modelo.entidades.UsuarioRegistrado;

public class BilleteraJPADAO implements IBilleteraDAO {

    private final EntityManager em;

    public BilleteraJPADAO(EntityManager em) {
        this.em = em;
    }
 // --- NUEVO MÉTODO AGREGADO SEGÚN DIAGRAMA (Paso 2.2) ---
    @Override
    public boolean validarMonto(double monto) {
        // Validación lógica: El monto debe ser mayor a 0 para recargar
        return monto > 0;
    }
    @Override
    public boolean recargarBilletera(double monto, UsuarioRegistrado usuario) {
        if (usuario == null) return false;
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            // Buscar la billetera asociada
            TypedQuery<Billetera> q = em.createQuery("SELECT b FROM Billetera b WHERE b.usuario.id = :uid", Billetera.class);
            q.setParameter("uid", usuario.getId());
            Billetera billetera = null;
            try {
                billetera = q.getSingleResult();
            } catch (NoResultException nre) {
                // No existe billetera para el usuario
                billetera = new Billetera();
                billetera.setSaldo(0);
                billetera.setUsuario(usuario);
                em.persist(billetera);
            }

            billetera.setSaldo(billetera.getSaldo() + monto);
            em.merge(billetera);
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean existenFondosValidos(UsuarioRegistrado usuario, double monto) {
        if (usuario == null) return false;
        try {
            TypedQuery<Double> q = em.createQuery("SELECT b.saldo FROM Billetera b WHERE b.usuario.id = :uid", Double.class);
            q.setParameter("uid", usuario.getId());
            Double saldo = q.getSingleResult();
            return saldo != null && saldo >= monto;
        } catch (NoResultException nre) {
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean retirarFondos(double monto, UsuarioRegistrado usuario) {
        if (usuario == null) return false;
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            TypedQuery<Billetera> q = em.createQuery("SELECT b FROM Billetera b WHERE b.usuario.id = :uid", Billetera.class);
            q.setParameter("uid", usuario.getId());
            Billetera billetera = q.getSingleResult();
            if (billetera.getSaldo() < monto) {
                tx.rollback();
                return false;
            }
            billetera.setSaldo(billetera.getSaldo() - monto);
            em.merge(billetera);
            tx.commit();
            return true;
        } catch (NoResultException nre) {
            if (tx.isActive()) tx.rollback();
            return false;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
}