package controlador;

import modelo.dao.JPAUtil;
import modelo.dao.jpa.EventoJPADAO;
import modelo.entidades.Evento;
import modelo.entidades.TipoCategoria;

import jakarta.persistence.EntityManager;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;

@WebServlet("/crearEvento")
public class CrearEventoController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.ruteador(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.ruteador(req, resp);
    }

    // --- PATRÓN RUTEADOR (Arquitectura MVC) ---
    private void ruteador(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Si no viene parámetro ruta, asumimos que quiere ver el formulario (inicio del CU)
        String ruta = (req.getParameter("ruta") != null) ? req.getParameter("ruta") : "crear";

        switch (ruta) {
            case "crear":
                // Corresponde al mensaje 1 del diagrama: crearEvento()
                this.crearEvento(req, resp);
                break;
            case "guardar":
                // Corresponde al mensaje 2 del diagrama: ingresar(...)
                this.ingresar(req, resp);
                break;
            default:
                this.crearEvento(req, resp);
                break;
        }
    }

    // --- MÉTODOS DEL DIAGRAMA DE SECUENCIA ---

    // 1: crearEvento()
    private void crearEvento(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Llama a 1.1
        this.mostrarFormularioEvento(req, resp);
    }

    // 1.1: mostrarFormularioEvento()
    private void mostrarFormularioEvento(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher("/jsp/nuevoEvento.jsp");
        rd.forward(req, resp);
    }

    // 2: ingresar(...)
    private void ingresar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            // Extracción de parámetros
            String nombre = req.getParameter("nombre");
            String descripcion = req.getParameter("descripcion");
            String fechaStr = req.getParameter("fecha");
            String categoriaStr = req.getParameter("categoria");

            // Conversión
            LocalDateTime fecha = (fechaStr != null && !fechaStr.isEmpty()) ? LocalDateTime.parse(fechaStr) : null;
            TipoCategoria categoria = (categoriaStr != null) ? TipoCategoria.valueOf(categoriaStr) : null;

            EventoJPADAO eventoDAO = new EventoJPADAO(em);

            // 2.1: validarDatos(...)
            boolean esValido = eventoDAO.validarDatos(nombre, descripcion, fecha, categoria);

            if (esValido) { // [validarDatos() = True]
                
                Evento nuevoEvento = new Evento();
                nuevoEvento.setNombre(nombre);
                nuevoEvento.setDescripcion(descripcion);
                nuevoEvento.setFecha(fecha);
                nuevoEvento.setCategoria(categoria);
                // Estado por defecto true (manejado en DAO o aquí)

                // 2.3: crearEvento(evento)
                boolean creado = eventoDAO.crearEvento(nuevoEvento);

                if (creado) {
                    // 2.5: mostrarConfirmacionCrear()
                    this.mostrarConfirmacionCrear(req, resp);
                } else {
                    this.mostrarDatosInvalidos(req, resp);
                }

            } else { // [validarDatos() = False]
                // 2.5.1: mostrarDatosInvalidos()
                this.mostrarDatosInvalidos(req, resp);
            }

        } catch (Exception e) {
            e.printStackTrace();
            this.mostrarDatosInvalidos(req, resp);
        } finally {
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    // 2.5: mostrarConfirmacionCrear()
    private void mostrarConfirmacionCrear(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        session.setAttribute("flash_status", "OK");
        session.setAttribute("flash_message", "Evento creado exitosamente.");
        
        // Al terminar la creación, redirigimos al controlador de gestión de eventos
        resp.sendRedirect(req.getContextPath() + "/gestionarEventos");
    }

    // 2.5.1: mostrarDatosInvalidos()
    private void mostrarDatosInvalidos(HttpServletRequest req, HttpServletResponse resp) throws IOException {
         HttpSession session = req.getSession();
         session.setAttribute("flash_status", "ERROR");
         session.setAttribute("flash_message", "Datos inválidos. Revise la fecha y campos vacíos.");
         
        resp.sendRedirect(req.getContextPath() + "/jsp/nuevoEvento.jsp");
     }
}