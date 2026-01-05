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

    @Column(name = "id_evento")
    private int idEvento;

    private double monto;

    @Enumerated(EnumType.STRING)
    private EstadoApuesta estado;

    private LocalDateTime fecha;

    // Nuevo atributo según la especificación: cuota registrada al momento de apostar
    private double cuotaRegistrada;
    // Nuevo atributo: id del pronóstico asociado
    @Column(name = "id_pronostico")
    private int idPronostico;

    public Apuesta() {}

    public Apuesta(int id, int idUsuario, int idEvento, double monto, EstadoApuesta estado, LocalDateTime fecha) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.idEvento = idEvento;
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

    public int getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(int idEvento) {
        this.idEvento = idEvento;
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

    public int getIdPronostico() {
        return idPronostico;
    }

    public void setIdPronostico(int idPronostico) {
        this.idPronostico = idPronostico;
    }
}