package modelo.entidades;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("RECARGA")
public class Recarga extends Movimiento {

    public Recarga() { super(); }

    public Recarga(int id, UsuarioRegistrado usuario, TipoMovimiento tipo, double monto, LocalDate fecha) {
        super(id, usuario, tipo, monto, fecha);
    }

}
