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

    // Nuevo atributo que indica si el pronóstico resultó ganador
    @Column(name = "es_ganador")
    private boolean esGanador;

    public Pronostico() {}

    public Pronostico(int id, Evento evento, String descripcion, double cuotaActual) {
        this.id = id;
        this.evento = evento;
        this.descripcion = descripcion;
        this.cuotaActual = cuotaActual;
        this.esGanador = false; // valor por defecto
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

    public boolean isEsGanador() {
        return esGanador;
    }

    public void setEsGanador(boolean esGanador) {
        this.esGanador = esGanador;
    }
}