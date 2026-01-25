package modelo.dao.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;
import modelo.dao.ApuestaDAO;
import modelo.entidades.Apuesta;
import modelo.entidades.Pronostico;
import modelo.entidades.Retiro;
import modelo.entidades.TipoMovimiento;
import modelo.entidades.UsuarioRegistrado;
import modelo.entidades.EstadoApuesta;
import modelo.entidades.Billetera;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.math.BigDecimal;
import java.math.RoundingMode;

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
 // --- MÉTODO CORREGIDO ---
    @Override
    public void guardarMovimiento(double monto, Pronostico pronostico, UsuarioRegistrado usuario) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            // 1. Verificar fondos en Billetera
            TypedQuery<Billetera> q = em.createQuery("SELECT b FROM Billetera b WHERE b.usuario.id = :uid", Billetera.class);
            q.setParameter("uid", usuario.getId());
            Billetera billetera = null;
            
            try {
                billetera = q.getSingleResult();
            } catch (Exception e) {
                // Si no tiene billetera, no puede apostar
                throw new IllegalStateException("El usuario no tiene billetera activa.");
            }

            // Normalizar monto y saldo a 2 decimales
            BigDecimal montoBD = BigDecimal.valueOf(monto).setScale(2, RoundingMode.HALF_UP);
            BigDecimal saldoBD = BigDecimal.valueOf(billetera.getSaldo()).setScale(2, RoundingMode.HALF_UP);

            if (billetera == null || saldoBD.compareTo(montoBD) < 0) {
                throw new IllegalStateException("Fondos insuficientes para realizar la apuesta.");
            }

            // 2. Descontar el monto de la Billetera
            BigDecimal nuevoSaldo = saldoBD.subtract(montoBD).setScale(2, RoundingMode.HALF_UP);
            billetera.setSaldo(nuevoSaldo.doubleValue());
            em.merge(billetera);

            // 3. Registrar el Movimiento como RETIRO (Para el historial)
            Retiro retiro = new Retiro();
            retiro.setUsuario(usuario);
            retiro.setTipo(TipoMovimiento.RETIRO); // Marcamos como retiro
            retiro.setMonto(montoBD.doubleValue());
            // Usamos LocalDate para coincidir con el formato de movimientos (ajusta si tu entidad usa LocalDateTime)
            retiro.setFecha(java.time.LocalDateTime.now()); 
            
            em.persist(retiro); 

            // 4. Crear la Apuesta
            Apuesta a = new Apuesta();
            a.setIdUsuario(usuario.getId());
            if (pronostico != null) {
                a.setPronostico(pronostico);
                a.setCuotaRegistrada(pronostico.getCuotaActual());
            }
            a.setMonto(montoBD.doubleValue());
            a.setEstado(EstadoApuesta.PENDIENTE);
            a.setFecha(LocalDateTime.now());
            
            em.persist(a);

            // 5. Confirmar transacción
            tx.commit();
            
        } catch (IllegalStateException ise) {
            if (tx.isActive()) tx.rollback();
            throw ise; // Re-lanzar para mensaje específico al usuario
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            throw new RuntimeException("Error interno al procesar la apuesta: " + e.getMessage());
        }
    }
 // --- MÉTODOS DEL DIAGRAMA CU12 ---

    // 3.4: obtenerApuestasPorPronostico (Apuestas Ganadoras)
    public List<Apuesta> obtenerApuestasPorPronostico(Pronostico pronostico) {
        String jpql = "SELECT a FROM Apuesta a WHERE a.pronostico.id = :pid AND a.estado = :pendiente";
        TypedQuery<Apuesta> q = em.createQuery(jpql, Apuesta.class);
        q.setParameter("pid", pronostico.getId());
        q.setParameter("pendiente", EstadoApuesta.PENDIENTE);
        return q.getResultList();
    }
    
    // 3.7: obtenerApuestas (Perdedoras - opcional, para cerrar las demás)
    public List<Apuesta> obtenerApuestasPerdedoras(int idEvento, int idPronosticoGanador) {
        String jpql = "SELECT a FROM Apuesta a WHERE a.pronostico.evento.id = :eid " +
                      "AND a.pronostico.id <> :pidGanador AND a.estado = :pendiente";
        TypedQuery<Apuesta> q = em.createQuery(jpql, Apuesta.class);
        q.setParameter("eid", idEvento);
        q.setParameter("pidGanador", idPronosticoGanador);
        q.setParameter("pendiente", EstadoApuesta.PENDIENTE);
        return q.getResultList();
    }

    // 3.6 y 4: cambiarEstado (Registrar pago o marcar perdida)
    public boolean cambiarEstado(List<Apuesta> apuestas, EstadoApuesta nuevoEstado) {
        if (apuestas == null || apuestas.isEmpty()) return true;
        
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            for (Apuesta a : apuestas) {
                a.setEstado(nuevoEstado);
                em.merge(a);
            }
            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            return false;
        }
    }
}