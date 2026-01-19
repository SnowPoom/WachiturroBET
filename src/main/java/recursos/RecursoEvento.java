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

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Evento> getEventos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            EventoJPADAO dao = new EventoJPADAO(em);
            return dao.obtenerTodosLosEventos();
        } finally {
            em.close();
        }
    }
    
    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Evento getEventoPorId(@PathParam("id") int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            EventoJPADAO dao = new EventoJPADAO(em);
            return dao.consultarDetallesEvento(id);
        } finally {
            em.close();
        }
    }
    
    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public boolean guardarEvento(Evento evento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            EventoJPADAO dao = new EventoJPADAO(em);
            return dao.crearEvento(evento);
        } finally {
            em.close();
        }
    }
    
    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public boolean actualizarEvento(Evento evento) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            EventoJPADAO dao = new EventoJPADAO(em);
            return dao.actualizarEvento(evento);
        } finally {
            em.close();
        }
    }
    
    @DELETE
    @Path("/delete/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean eliminarEvento(@PathParam("id") int id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            EventoJPADAO dao = new EventoJPADAO(em);
            return dao.eliminarEvento(id);
        } finally {
            em.close();
        }
    }
}