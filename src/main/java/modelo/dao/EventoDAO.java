package modelo.dao;

import java.util.List;

import modelo.entidades.Evento;

public interface EventoDAO {
    Evento consultarDetallesEvento(int id);
    
    List<Evento> obtenerEventosDisponibles();
    List<Evento> obtenerTodosLosEventos();
    
    boolean crearEvento(Evento evento);
    boolean actualizarEvento(Evento evento);
    boolean eliminarEvento(int id);
}