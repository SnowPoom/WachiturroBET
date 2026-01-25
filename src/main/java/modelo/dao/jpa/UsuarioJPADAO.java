package modelo.dao.jpa;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import modelo.entidades.Usuario;

public class UsuarioJPADAO {
    
    private final EntityManager emf; 
    
    public UsuarioJPADAO(EntityManager emf) {
    			this.emf = emf;
    }

    public Usuario autenticar(String correo, String clave) {
        Usuario usuario = null;
        try {
            // Buscamos un Usuario cuyo correo y clave coincidan
            String jpql = "SELECT u FROM Usuario u WHERE u.correo = :correo AND u.clave = :clave";
            TypedQuery<Usuario> query = emf.createQuery(jpql, Usuario.class);
            query.setParameter("correo", correo);
            query.setParameter("clave", clave);
            
            // getSingleResult lanza excepción si no encuentra nada, por eso el try-catch
            usuario = query.getSingleResult();
            
        } catch (Exception e) {
            // Si no encuentra usuario o hay error, retornamos null (Login fallido)
            usuario = null; 
        } 
        return usuario;
    }
}