package modelo.entidades;

import java.time.LocalDateTime;

public class Movimiento {
    private int id;
    private int idBilletera;
    private TipoMovimiento tipo;
    private double monto;
    private LocalDateTime fecha;

    public Movimiento() {}

    public Movimiento(int id, int idBilletera, TipoMovimiento tipo, double monto, LocalDateTime fecha) {
        this.id = id;
        this.idBilletera = idBilletera;
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

    public int getIdBilletera() {
        return idBilletera;
    }

    public void setIdBilletera(int idBilletera) {
        this.idBilletera = idBilletera;
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
                ", idBilletera=" + idBilletera +
                ", tipo=" + tipo +
                ", monto=" + monto +
                ", fecha=" + fecha +
                '}';
    }
}
