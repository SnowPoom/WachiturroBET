package modelo.dao.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import modelo.entidades.Billetera;
import modelo.entidades.Usuario;
import modelo.entidades.UsuarioRegistrado;

public class UsuarioJPADAO {
    
    private final EntityManager emf; 
    
    public UsuarioJPADAO(EntityManager emf) {
        this.emf = emf;
    }

    public Usuario autenticar(String correo, String clave) {
        Usuario usuario = null;
        try {
            String jpql = "SELECT u FROM Usuario u WHERE u.correo = :correo AND u.clave = :clave";
            TypedQuery<Usuario> query = emf.createQuery(jpql, Usuario.class);
            query.setParameter("correo", correo);
            query.setParameter("clave", clave);
            usuario = query.getSingleResult();
        } catch (Exception e) {
            usuario = null; 
        } 
        return usuario;
    }

    public void finalizarSesion(int idUsuario) {
        if (idUsuario <= 0) return;
        Usuario u = emf.find(Usuario.class, idUsuario);
        if (u != null) {
            // Lógica adicional de cierre si fuera necesaria
            emf.merge(u);
        }
    }

    // --- MÉTODOS SOLICITADOS PARA EL REGISTRO ---

    // Verifica si el correo ya existe en la base de datos
    public boolean existeCorreo(String correo) {
        try {
            TypedQuery<Long> q = emf.createQuery("SELECT COUNT(u) FROM Usuario u WHERE u.correo = :correo", Long.class);
            q.setParameter("correo", correo);
            return q.getSingleResult() > 0;
        } catch (Exception e) {
            return false;
        }
    }

    // Método transaccional que crea Usuario y Billetera (siguiendo el diagrama 2.1.1.1)
    public boolean crearCuenta(String nombre, String apellido, String correo, String clave) {
        EntityTransaction tx = emf.getTransaction();
        try {
            tx.begin();

            // 1. Crear el Usuario Registrado
            UsuarioRegistrado usuario = new UsuarioRegistrado();
            usuario.setNombre(nombre);
            usuario.setApellido(apellido);
            usuario.setCorreo(correo);
            usuario.setClave(clave);
            
            emf.persist(usuario);

            // 2. Crear la Billetera inicial (Monto 0)
            // Según el diagrama, el usuarioDAO llama a crearBilletera.
            // Al persistirla aquí dentro de la misma transacción, garantizamos integridad.
            Billetera billetera = new Billetera();
            billetera.setSaldo(0.0);
            billetera.setUsuario(usuario); // Vinculación
            
            emf.persist(billetera);

            tx.commit();
            return true;
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
            return false;
        }
    }
}