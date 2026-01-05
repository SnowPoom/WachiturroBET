package modelo.entidades;

import java.time.LocalDateTime;
import jakarta.persistence.*;

@Entity
@Table(name = "eventos")
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String nombre;

    private LocalDateTime fecha;

    private TipoCategoria categoria;

    // Añadimos descripción según la especificación
    private String descripcion;

    public Evento() {}

    public Evento(int id, String nombre, LocalDateTime fecha, TipoCategoria categoria) {
        this.id = id;
        this.nombre = nombre;
        this.fecha = fecha;
        this.categoria = categoria;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public TipoCategoria getCategoria() {
        return categoria;
    }

    public void setCategoria(TipoCategoria categoria) {
        this.categoria = categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}