package modelo.dao;

import modelo.entidades.Apuesta;
import modelo.entidades.Pronostico;
import modelo.entidades.UsuarioRegistrado;
import java.time.LocalDateTime;
import java.util.List;

public interface ApuestaDAO {
    List<Apuesta> obtenerApuestas();

    List<Apuesta> filtrar(LocalDateTime fechaInicio, LocalDateTime fechaFin, String estado);

    void apostar(double monto, Pronostico pronostico, UsuarioRegistrado usuario);
}