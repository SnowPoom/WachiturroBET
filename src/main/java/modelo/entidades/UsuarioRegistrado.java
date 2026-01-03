package modelo.entidades;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("0") // <--- Aquí definimos que el 0 es para Registrados
public class UsuarioRegistrado extends Usuario {
    
    private static final long serialVersionUID = 1L;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Billetera billetera;

    public UsuarioRegistrado() {
        super();
        // Ya no hace falta hacer setAdmin(false), JPA lo sabe por el @DiscriminatorValue
    }

    public UsuarioRegistrado(int id, String nombre, String apellido, String correo, String clave) {
        super(id, nombre, apellido, correo, clave);
    }
    public Billetera getBilletera() {
        return billetera;
    }

    public void setBilletera(Billetera billetera) {
        this.billetera = billetera;
    }
}
