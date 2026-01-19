package controlador;

import modelo.dao.JPAUtil;
import modelo.dao.jpa.ApuestaJPADAO;
import modelo.dao.jpa.EventoJPADAO;
import modelo.dao.jpa.PronosticoJPADAO;
import modelo.dao.jpa.RecargaJPADAO;
import modelo.entidades.Apuesta;
import modelo.entidades.EstadoApuesta;
import modelo.entidades.Evento;
import modelo.entidades.Pronostico;

import jakarta.persistence.EntityManager;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/finalizarEvento")
public class FinalizarEventoController extends HttpServlet {

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
        // Determinamos la acción: 'entrar' (ver pantalla) o 'confirmar' (procesar)
        String ruta = (req.getParameter("ruta") != null) ? req.getParameter("ruta") : "entrar";
        
        // Si viene desde form action /procesarFinalizarEvento, asumimos 'confirmar'
        if (req.getServletPath().contains("procesarFinalizarEvento")) {
            ruta = "confirmar";
        }

        switch (ruta) {
            case "entrar":
                this.entrar(req, resp);
                break;
            case "confirmar":
                this.confirmar(req, resp);
                break;
            default:
                this.entrar(req, resp);
                break;
        }
    }

    // 1: entrar(evento) -> Carga la vista para seleccionar ganador
    private void entrar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String idEventoStr = req.getParameter("idEvento");
        if (idEventoStr == null) {
            resp.sendRedirect(req.getContextPath() + "/gestionarEventos");
            return;
        }

        EntityManager em = JPAUtil.getEntityManager();
        try {
            int id = Integer.parseInt(idEventoStr);
            EventoJPADAO eventoDAO = new EventoJPADAO(em);
            PronosticoJPADAO pronosticoDAO = new PronosticoJPADAO(em);

            Evento evento = eventoDAO.consultarDetallesEvento(id);
            
            // VALIDACIÓN: Verificar si el evento está abierto
            if (evento == null || !evento.isEstado()) {
                setFlash(req, "ERROR", "El evento ya está finalizado o no existe.");
                resp.sendRedirect(req.getContextPath() + "/gestionarEventos");
                return;
            }

            List<Pronostico> pronosticos = pronosticoDAO.obtenerPronosticosPorEvento(evento);

            req.setAttribute("evento", evento);
            req.setAttribute("nombreEvento", evento.getNombre());
            req.setAttribute("descEvento", evento.getDescripcion());
            req.setAttribute("listaPronosticos", pronosticos);

            RequestDispatcher rd = req.getRequestDispatcher("/jsp/estadoEvento.jsp");
            rd.forward(req, resp);

        } catch (Exception e) {
            resp.sendRedirect(req.getContextPath() + "/gestionarEventos");
        } finally {
            em.close();
        }
    }

    // 3: confirmar -> Ejecuta la lógica del Diagrama CU12
    private void confirmar(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            int idEvento = Integer.parseInt(req.getParameter("idEvento"));
            int idPronosticoGanador = Integer.parseInt(req.getParameter("idPronosticoGanador"));

            // Instanciar DAOs
            EventoJPADAO eventoDAO = new EventoJPADAO(em);
            PronosticoJPADAO pronosticoDAO = new PronosticoJPADAO(em);
            ApuestaJPADAO apuestaDAO = new ApuestaJPADAO(em);
            RecargaJPADAO recargaDAO = new RecargaJPADAO(em); // Nuevo DAO solicitado

            // Validar nuevamente que el evento esté abierto antes de procesar
            Evento evento = eventoDAO.consultarDetallesEvento(idEvento);
            if (!evento.isEstado()) {
                setFlash(req, "ERROR", "Acción denegada: El evento ya estaba cerrado.");
                resp.sendRedirect(req.getContextPath() + "/gestionarEventos");
                return;
            }

            // --- SECUENCIA DEL DIAGRAMA ---

            // 3.1: finalizarEvento(evento)
            eventoDAO.finalizarEvento(evento);

            // 3.2: obtenerPronosticoGanador (y marcarlo)
            Pronostico ganador = pronosticoDAO.marcarComoGanador(idPronosticoGanador);

            // 3.4: obtenerApuestasGanadoras(pronostico)
            List<Apuesta> apuestasGanadoras = apuestaDAO.obtenerApuestasPorPronostico(ganador);
            
            // 3.5: recargarGanadores(apuestasGanadoras) -> Usando RecargaDAO
            recargaDAO.recargarGanadores(apuestasGanadoras);

            // 3.6: registrarPagoApuestas -> Cambiar estado a PAGADA (GANADA)
            apuestaDAO.cambiarEstado(apuestasGanadoras, EstadoApuesta.GANADA);

            // 4: cambiarEstado (Perdedoras) -> Esto cierra el ciclo para las que no ganaron
            List<Apuesta> apuestasPerdedoras = apuestaDAO.obtenerApuestasPerdedoras(idEvento, idPronosticoGanador);
            apuestaDAO.cambiarEstado(apuestasPerdedoras, EstadoApuesta.PERDIDA);

            setFlash(req, "OK", "Evento finalizado. Se han pagado " + apuestasGanadoras.size() + " apuestas ganadoras.");

        } catch (Exception e) {
            e.printStackTrace();
            setFlash(req, "ERROR", "Error crítico al finalizar evento: " + e.getMessage());
        } finally {
            em.close();
            resp.sendRedirect(req.getContextPath() + "/gestionarEventos");
        }
    }

    private void setFlash(HttpServletRequest req, String status, String message) {
        HttpSession session = req.getSession();
        session.setAttribute("flash_status", status);
        session.setAttribute("flash_message", message);
    }
}