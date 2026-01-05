package modelo.entidades;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(name = "pronosticos")
public class Pronostico implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // FK al evento
    @Column(name = "id_evento")
    private int id_evento;

    private String descripcion;

    private double cuotaActual;

    public Pronostico() {}

    public Pronostico(int id, int id_evento, String descripcion, double cuotaActual) {
        this.id = id;
        this.id_evento = id_evento;
        this.descripcion = descripcion;
        this.cuotaActual = cuotaActual;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_evento() {
        return id_evento;
    }

    public void setId_evento(int id_evento) {
        this.id_evento = id_evento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getCuotaActual() {
        return cuotaActual;
    }

    public void setCuotaActual(double cuotaActual) {
        this.cuotaActual = cuotaActual;
    }
}