package controlador;

import modelo.dao.JPAUtil;
import modelo.dao.jpa.EventoJPADAO;
import modelo.entidades.Evento;

import jakarta.persistence.EntityManager;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/buscarEvento")
public class BuscarEventoController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.ruteador(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.ruteador(req, resp);
    }

    private void ruteador(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String ruta = (req.getParameter("ruta") != null) ? req.getParameter("ruta") : "buscar";

        switch (ruta) {
            case "buscar":
                this.ingresarBusqueda(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/index.jsp");
                break;
        }
    }

    private void ingresarBusqueda(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String texto = req.getParameter("texto");

        if (!this.validarTexto(texto)) {
            // Si no es válido, redirigimos o mostramos error (asumimos sin coincidencias para flujo simple)
            this.mostrarSinCoincidencias(req, resp, "Por favor ingrese un término de búsqueda válido.");
            return;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            EventoJPADAO dao = new EventoJPADAO(em);
            
            List<Evento> resultados = dao.buscarEventosPorNombre(texto);

            if (resultados != null && !resultados.isEmpty()) {
                this.desplegarLista(req, resp, resultados);
            } else {
                this.mostrarSinCoincidencias(req, resp, "No se encontraron eventos con el nombre: \"" + texto + "\"");
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.mostrarSinCoincidencias(req, resp, "Ocurrió un error al realizar la búsqueda.");
        } finally {
            em.close();
        }
    }

    private boolean validarTexto(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    private void desplegarLista(HttpServletRequest req, HttpServletResponse resp, List<Evento> lista) throws ServletException, IOException {
        req.setAttribute("resultadosBusqueda", lista);
        RequestDispatcher rd = req.getRequestDispatcher("/jsp/ResultadoBusqueda.jsp");
        rd.forward(req, resp);
    }

    private void mostrarSinCoincidencias(HttpServletRequest req, HttpServletResponse resp, String mensaje) throws ServletException, IOException {
        req.setAttribute("mensajeError", mensaje);
        // Reutilizamos el mismo JSP pero sin lista, para mostrar el mensaje
        RequestDispatcher rd = req.getRequestDispatcher("/jsp/ResultadoBusqueda.jsp");
        rd.forward(req, resp);
    }
}