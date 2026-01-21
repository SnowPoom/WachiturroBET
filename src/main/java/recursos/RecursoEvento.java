package recursos;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import modelo.dao.JPAUtil;
import modelo.dao.jpa.EventoJPADAO;
import modelo.entidades.Evento;

@Path("/eventos")
public class RecursoEvento {

	private EventoJPADAO eventoDAO;
	
	public RecursoEvento() {
		EntityManager em = JPAUtil.getEntityManager();
		this.eventoDAO = new EventoJPADAO(em);
	}
	
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Evento> getEventos() {
    	return this.eventoDAO.obtenerTodosLosEventos();
    }
    
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Evento getEventoPorId(@PathParam("id") int id) {
    	return this.eventoDAO.consultarDetallesEvento(id);
    }
    
    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public boolean guardarEvento(Evento evento) {
    	return this.eventoDAO.crearEvento(evento);
    }
    
    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public boolean actualizarEvento(Evento evento) {
    	return this.eventoDAO.actualizarEvento(evento);
    }
    
    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean eliminarEvento(@PathParam("id") int id) {
    	return this.eventoDAO.eliminarEvento(id);
    }
}