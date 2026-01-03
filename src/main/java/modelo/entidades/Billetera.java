package modelo.entidades;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity // <--- 1. OBLIGATORIO: Marca la clase como entidad
@Table(name = "billeteras") // <--- 2. Vincula con la tabla de BD
public class Billetera implements Serializable {
    
    private static final long serialVersionUID = 1L;

    @Id // <--- 3. Marca la Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_billetera")
    private int id;

    @Column(name = "saldo")
    private double saldo;

    // 4. CAMBIO CRÍTICO:
    // En lugar de 'private int idUsuario', usamos el Objeto.
    // Esta es la "Owning Side" (la que tiene la FK en la base de datos).
    @OneToOne
    // unique = true: Garantiza que no puedas guardar dos billeteras con el mismo usuario
    @JoinColumn(name = "id_usuario", referencedColumnName = "id_usuario", unique = true) 
    private UsuarioRegistrado usuario;

    public Billetera() {}

    public Billetera(int id, double saldo, UsuarioRegistrado usuario) {
        this.id = id;
        this.saldo = saldo;
        this.usuario = usuario;
    }

    // Getters y Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public UsuarioRegistrado getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioRegistrado usuario) {
        this.usuario = usuario;
    }

    @Override
    public String toString() {
        return "Billetera{" + "id=" + id + ", saldo=" + saldo + "}";
    }
}