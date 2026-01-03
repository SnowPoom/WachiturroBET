package test;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import modelo.entidades.Billetera;
import modelo.entidades.Usuario;
import modelo.entidades.UsuarioRegistrado;

public class ORMTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		EntityManagerFactory emf= Persistence.createEntityManagerFactory("WachiturroBET");
		EntityManager em= emf.createEntityManager();
		String correoPrueba = "turip" + System.currentTimeMillis() + "@test.com";
        
        UsuarioRegistrado user = new UsuarioRegistrado(0, "Tsssp", "sss", correoPrueba, "vegita87");

        // 3. Crear su Billetera (Probamos la relación 1 a 1)
        Billetera billetera = new Billetera();
        billetera.setSaldo(100.00); // Le regalamos 100 de saldo inicial
        
        // 4. Conectar los objetos (Bidireccional)
        billetera.setUsuario(user); // La billetera conoce a su dueño
        user.setBilletera(billetera); // El dueño conoce su billetera

        // 5. Iniciar Transacción
        em.getTransaction().begin();

        // 6. Guardar (Persist)
        // Gracias al CascadeType.ALL en UsuarioRegistrado, 
        // al guardar el user, se guarda SOLA la billetera.
        em.persist(user);

        em.getTransaction().commit();
	}

}
