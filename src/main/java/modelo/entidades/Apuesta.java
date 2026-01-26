package modelo.entidades;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "apuestas")
public class Apuesta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "id_usuario")
    private int idUsuario;

    private double monto;

    @Enumerated(EnumType.STRING)
    private EstadoApuesta estado;

    private LocalDateTime fecha;

    private double cuotaRegistrada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pronostico")
    private Pronostico pronostico;

    public Apuesta() {}

    public Apuesta(int id, int idUsuario, double monto, EstadoApuesta estado, LocalDateTime fecha) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.monto = monto;
        this.estado = estado;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public EstadoApuesta getEstado() {
        return estado;
    }

    public void setEstado(EstadoApuesta estado) {
        this.estado = estado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double getCuotaRegistrada() {
        return cuotaRegistrada;
    }

    public void setCuotaRegistrada(double cuotaRegistrada) {
        this.cuotaRegistrada = cuotaRegistrada;
    }

    public Pronostico getPronostico() {
        return pronostico;
    }

    public void setPronostico(Pronostico pronostico) {
        this.pronostico = pronostico;
    }
}