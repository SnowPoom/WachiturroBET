package modelo.entidades;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import jakarta.persistence.*;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.json.bind.annotation.JsonbTransient;

@Entity
@Table(name = "eventos")
@XmlRootElement
public class Evento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    
    @Column(name = "nombre")
    private String nombre;

    @Column(name = "fecha")
    private LocalDateTime fecha;

    @Column(name = "categoria")
    private TipoCategoria categoria;

    @Column(name = "descripcion")
    private String descripcion;
    
    @Column(name = "estado")
    private boolean estado;
    
    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pronostico> pronosticos;
   

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

	public boolean isEstado() {
		return estado;
	}

	public void setEstado(boolean estado) {
		this.estado = estado;
	}
	
    public List<Pronostico> getPronosticos() {
		return pronosticos;
	}

	public void setPronosticos(List<Pronostico> pronosticos) {
		this.pronosticos = pronosticos;
	}

	@JsonbTransient
    public String getFechaFormateada() {
        if (this.fecha == null) return "-";
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return this.fecha.format(fmt);
    }

	@Override
	public String toString() {
		return "Evento [id=" + id + ", nombre=" + nombre + ", fecha=" + fecha + ", categoria=" + categoria
				+ ", descripcion=" + descripcion + ", estado=" + estado + ", pronosticos=" + pronosticos + "]";
	}
    
   
}