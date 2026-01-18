package modelo.entidades;

import java.io.Serializable;
import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "movimientos")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "movimiento_tipo", discriminatorType = DiscriminatorType.STRING)
public abstract class Movimiento implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_movimiento")
    private int id;

    // FK to UsuarioRegistrado (id_usuario)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private UsuarioRegistrado usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoMovimiento tipo;

    @Column(name = "monto")
    private double monto;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    public Movimiento() {}

    public Movimiento(int id, UsuarioRegistrado usuario, TipoMovimiento tipo, double monto, LocalDateTime fecha) {
        this.id = id;
        this.usuario = usuario;
        this.tipo = tipo;
        this.monto = monto;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UsuarioRegistrado getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioRegistrado usuario) {
        this.usuario = usuario;
    }

    public TipoMovimiento getTipo() {
        return tipo;
    }

    public void setTipo(TipoMovimiento tipo) {
        this.tipo = tipo;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    @Override
    public String toString() {
        return "Movimiento{" +
                "id=" + id +
                ", usuarioId=" + (usuario != null ? usuario.getId() : "null") +
                ", tipo=" + tipo +
                ", monto=" + monto +
                ", fecha=" + fecha +
                '}';
    }
}