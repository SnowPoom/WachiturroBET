package modelo.dao;

import modelo.entidades.Evento;
import modelo.entidades.Pronostico;
import java.util.List;

public interface PronosticoDAO {
    List<Pronostico> obtenerPronosticosPorEvento(Evento evento);

    String obtenerDescripcion(int idPronostico);
}