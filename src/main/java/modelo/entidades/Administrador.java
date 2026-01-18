package modelo.entidades;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("1")
public class Administrador extends Usuario {

    private static final long serialVersionUID = 1L;

    public Administrador() {
        super();
    }
    
    public Administrador(int id, String nombre, String apellido, String correo, String clave) {
        super(id, nombre, apellido, correo, clave);
    }
}
