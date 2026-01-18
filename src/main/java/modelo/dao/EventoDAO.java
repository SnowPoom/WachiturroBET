package modelo.dao;

import java.util.List;

import modelo.entidades.Evento;

public interface EventoDAO {
    String obtenerNombreEvento();

    Evento consultarDetallesEvento(int id);
    
    List<Evento> obtenerEventosDisponibles();
    
    
    
    
}