package controlador;

import jakarta.persistence.EntityManager;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import modelo.dao.JPAUtil;
import modelo.dao.jpa.ApuestaJPADAO;
import modelo.entidades.Apuesta;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/historial")
public class HistorialController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if (action == null || "ingresar".equalsIgnoreCase(action)) {
            ingresar(req, resp);
            return;
        }
        if ("limpiarFiltros".equalsIgnoreCase(action)) {
            limpiarFiltros(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");
        if ("filtrar".equalsIgnoreCase(action)) {
            filtrar(req, resp);
            return;
        }
        resp.sendError(HttpServletResponse.SC_BAD_REQUEST);
    }

    private void ingresar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            ApuestaJPADAO dao = new ApuestaJPADAO(em);
            List<Apuesta> lista = dao.obtenerApuestas();
            if (lista == null || lista.isEmpty()) {
                req.setAttribute("sinRegistros", true);
            }
            req.setAttribute("apuestas", lista);
            RequestDispatcher rd = req.getRequestDispatcher("/jsp/historial.jsp");
            rd.forward(req, resp);
        } finally {
            em.close();
        }
    }

    private void filtrar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            String fInicio = req.getParameter("fechaInicio");
            String fFin = req.getParameter("fechaFin");
            String estado = req.getParameter("estado");

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
            LocalDateTime inicio = null;
            LocalDateTime fin = null;
            try {
                if (fInicio != null && !fInicio.isEmpty()) inicio = LocalDateTime.parse(fInicio, fmt);
            } catch (Exception ignored) {}
            try {
                if (fFin != null && !fFin.isEmpty()) fin = LocalDateTime.parse(fFin, fmt);
            } catch (Exception ignored) {}

            ApuestaJPADAO dao = new ApuestaJPADAO(em);
            List<Apuesta> resultados = dao.filtrar(inicio, fin, estado);
            if (resultados == null || resultados.isEmpty()) {
                req.setAttribute("sinCoincidencias", true);
            }
            req.setAttribute("apuestas", resultados);
            RequestDispatcher rd = req.getRequestDispatcher("/jsp/historial.jsp");
            rd.forward(req, resp);
        } finally {
            em.close();
        }
    }

    private void limpiarFiltros(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Simplemente reenviar a ingresar para recargar todo
        ingresar(req, resp);
    }
}
