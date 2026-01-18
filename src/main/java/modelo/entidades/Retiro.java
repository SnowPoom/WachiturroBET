package modelo.entidades;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("RETIRO")
public class Retiro extends Movimiento {
    private static final long serialVersionUID = 1L;

    public Retiro() { super(); }

    public Retiro(int id, UsuarioRegistrado usuario, TipoMovimiento tipo, double monto, java.time.LocalDateTime fecha) {
        super(id, usuario, tipo, monto, fecha);
    }
}
