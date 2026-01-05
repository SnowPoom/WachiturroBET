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

    private String descripcion;

    private double cuotaActual;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_evento")
    private Evento evento;

    public Pronostico() {}

    public Pronostico(int id, Evento evento, String descripcion, double cuotaActual) {
        this.id = id;
        this.evento = evento;
        this.descripcion = descripcion;
        this.cuotaActual = cuotaActual;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }
}