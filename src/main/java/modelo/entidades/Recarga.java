package modelo.entidades;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@DiscriminatorValue("RECARGA")
public class Recarga extends Movimiento {
	private static final long serialVersionUID = 1L;
    public Recarga() { super(); }

    public Recarga(int id, UsuarioRegistrado usuario, TipoMovimiento tipo, double monto, LocalDateTime fecha) {
        super(id, usuario, tipo, monto, fecha);
    }

}