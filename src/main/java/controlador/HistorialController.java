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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            // Build fechasMap for formatted dates
            Map<Integer, String> fechasMap = buildFechasMap(lista);
            req.setAttribute("fechasMap", fechasMap);

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

            // Keep original strings to repopulate the form
            req.setAttribute("fechaInicio", fInicio);
            req.setAttribute("fechaFin", fFin);
            req.setAttribute("estadoFiltro", estado);

            LocalDateTime inicio = null;
            LocalDateTime fin = null;
            try {
                if (fInicio != null && !fInicio.isEmpty()) {
                    LocalDate dInicio = LocalDate.parse(fInicio); // expects yyyy-MM-dd from input[type=date]
                    inicio = dInicio.atStartOfDay();
                }
            } catch (Exception ignored) {}
            try {
                if (fFin != null && !fFin.isEmpty()) {
                    LocalDate dFin = LocalDate.parse(fFin);
                    // set end of day to include all times of that date
                    fin = dFin.atTime(LocalTime.MAX);
                }
            } catch (Exception ignored) {}

            // Validation: if both present, ensure inicio <= fin
            if (inicio != null && fin != null && inicio.isAfter(fin)) {
                req.setAttribute("filtroError", "La fecha inicio no puede ser posterior a la fecha fin.");
                // return with no results
                req.setAttribute("apuestas", java.util.Collections.emptyList());
                req.setAttribute("sinCoincidencias", true);
                RequestDispatcher rd = req.getRequestDispatcher("/jsp/historial.jsp");
                rd.forward(req, resp);
                return;
            }

            ApuestaJPADAO dao = new ApuestaJPADAO(em);
            List<Apuesta> resultados = dao.filtrar(inicio, fin, estado);
            if (resultados == null || resultados.isEmpty()) {
                req.setAttribute("sinCoincidencias", true);
            }
            req.setAttribute("apuestas", resultados);
            // Build fechasMap
            Map<Integer, String> fechasMap = buildFechasMap(resultados);
            req.setAttribute("fechasMap", fechasMap);

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

    // Helper to build map of formatted dates for the view
    private Map<Integer, String> buildFechasMap(List<Apuesta> lista) {
        Map<Integer, String> map = new HashMap<>();
        if (lista == null) return map;
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        for (Apuesta a : lista) {
            if (a != null && a.getFecha() != null) {
                try {
                    map.put(a.getId(), a.getFecha().format(df));
                } catch (Exception e) {
                    map.put(a.getId(), "-");
                }
            } else if (a != null) {
                map.put(a.getId(), "-");
            }
        }
        return map;
    }
}