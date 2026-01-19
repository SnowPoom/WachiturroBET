package modelo.dao.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import modelo.entidades.Apuesta;
import modelo.entidades.Billetera;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class RecargaJPADAO {
    private final EntityManager em;

    public RecargaJPADAO(EntityManager em) {
        this.em = em;
    }

    // 3.5: recargarGanadores
    public boolean recargarGanadores(List<Apuesta> apuestasGanadoras) {
        if (apuestasGanadoras == null || apuestasGanadoras.isEmpty()) return true;

        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            
            for (Apuesta apuesta : apuestasGanadoras) {
                // Calcular ganancia: Monto apostado * Cuota del pronóstico
                BigDecimal montoBD = BigDecimal.valueOf(apuesta.getMonto()).setScale(2, RoundingMode.HALF_UP);
                BigDecimal cuotaBD = BigDecimal.valueOf(apuesta.getCuotaRegistrada());
                BigDecimal gananciaBD = montoBD.multiply(cuotaBD).setScale(2, RoundingMode.HALF_UP);
                double ganancia = gananciaBD.doubleValue();
                
                // Buscar billetera del usuario
                String jpql = "SELECT b FROM Billetera b WHERE b.usuario.id = :uid";
                TypedQuery<Billetera> q = em.createQuery(jpql, Billetera.class);
                q.setParameter("uid", apuesta.getIdUsuario());
                
                Billetera billetera = q.getSingleResult();
                
                // Actualizar saldo
                if (billetera != null) {
                    BigDecimal saldoBD = BigDecimal.valueOf(billetera.getSaldo()).setScale(2, RoundingMode.HALF_UP);
                    BigDecimal nuevoBD = saldoBD.add(gananciaBD).setScale(2, RoundingMode.HALF_UP);
                    billetera.setSaldo(nuevoBD.doubleValue());
                    em.merge(billetera);
                    // Opcional: Aquí se podría crear un Movimiento tipo "PREMIO"
                }
            }
            
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
}