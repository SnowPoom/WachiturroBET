package modelo.dao;

import modelo.entidades.Evento;

public interface EventoDAO {
    String obtenerNombreEvento();

    // Cambiado para ajustarse a la especificación: retornar Evento
    Evento consultarDetallesEvento(int id);
}