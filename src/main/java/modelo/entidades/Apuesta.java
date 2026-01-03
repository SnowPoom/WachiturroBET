package modelo.entidades;

import java.time.LocalDateTime;

public class Apuesta {
    private int id;
    private int idUsuario;
    private int idEvento;
    private double monto;
    private EstadoApuesta estado;
    private LocalDateTime fecha;

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
}
