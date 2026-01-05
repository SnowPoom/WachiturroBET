package modelo.dao;

import modelo.entidades.Evento;

public interface EventoDAO {
    String obtenerNombreEvento();

    Evento consultarDetallesEvento(int id);
}