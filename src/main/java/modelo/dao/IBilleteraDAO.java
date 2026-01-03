package modelo.dao;

import modelo.entidades.UsuarioRegistrado;

public interface IBilleteraDAO {
    // Recarga la billetera del usuario, retorna true si se actualizó correctamente
    boolean recargarBilletera(double monto, UsuarioRegistrado usuario);

    // Verifica si el usuario tiene al menos 'monto' fondos disponibles
    boolean verificarFondos(UsuarioRegistrado usuario, double monto);

    // Retira fondos de la billetera, retorna true si la operación fue exitosa
    boolean retirarFondos(double monto, UsuarioRegistrado usuario);
}
