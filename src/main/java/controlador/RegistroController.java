package controlador;

import modelo.dao.JPAUtil;
import modelo.dao.jpa.UsuarioJPADAO;

import jakarta.persistence.EntityManager;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/registrarCuenta")
public class RegistroController extends HttpServlet {
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
        String ruta = (req.getParameter("ruta") != null) ? req.getParameter("ruta") : "entrar";

        switch (ruta) {
            case "entrar":
                // 1: entrar()
                this.entrar(req, resp);
                break;
            case "ingresar":
                // 2: ingresar(...) - Se reciben los datos del formulario
                String nombre = req.getParameter("nombre");
                String apellido = req.getParameter("apellido");
                String correo = req.getParameter("correo");
                String clave = req.getParameter("clave");
                this.ingresar(req, resp, nombre, apellido, correo, clave);
                break;
            default:
                this.entrar(req, resp);
                break;
        }
    }

    // 1: entrar()
    private void entrar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // 1.1: mostrarRegistro()
        this.mostrarRegistro(req, resp);
    }

    // 1.1: mostrarRegistro()
    private void mostrarRegistro(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher("/jsp/registrar.jsp");
        rd.forward(req, resp);
    }

    // 2: ingresar(...)
    private void ingresar(HttpServletRequest req, HttpServletResponse resp, String nombre, String apellido, String correo, String clave) throws IOException, ServletException {
        
        // 2.1: validarDatos(...)
        if (this.validarDatos(nombre, apellido, correo, clave)) {
            
            EntityManager em = JPAUtil.getEntityManager();
            try {
                UsuarioJPADAO usuarioDAO = new UsuarioJPADAO(em);

                // Verificación de correo duplicado (Lógica del diagrama)
                // Usamos el método del DAO que acabamos de crear
                if (!usuarioDAO.existeCorreo(correo)) {
                    
                    // 2.1.1.1: crearCuenta(...) - Delegamos al DAO
                    boolean creado = usuarioDAO.crearCuenta(nombre, apellido, correo, clave);
                    
                    if (creado) {
                        // 2.1.1.1.3.1: mostrarMensajeRegistro()
                        this.mostrarMensajeRegistro(req, resp);
                    } else {
                        this.mostrarMensajeDatosInvalidos(req, resp, "Error interno al crear la cuenta.");
                    }
                } else {
                    // 2.1.1.1.4.1: mostrarMensajeCorreoUsado()
                    this.mostrarMensajeCorreoUsado(req, resp);
                }
            } finally {
                em.close();
            }

        } else {
            // 2.1.1.1.5.1: mostrarMensajeDatosInvalidos()
            this.mostrarMensajeDatosInvalidos(req, resp, "Datos incompletos.");
        }
    }

    // 2.1: validarDatos(...)
    private boolean validarDatos(String nombre, String apellido, String correo, String clave) {
        return nombre != null && !nombre.trim().isEmpty() &&
               apellido != null && !apellido.trim().isEmpty() &&
               correo != null && !correo.trim().isEmpty() &&
               clave != null && !clave.trim().isEmpty();
    }

    // 2.1.1.1.3.1: mostrarMensajeRegistro()
    private void mostrarMensajeRegistro(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        session.setAttribute("flash_status", "OK");
        session.setAttribute("flash_message", "¡Cuenta creada exitosamente! Por favor inicia sesión.");
        resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
    }

    // 2.1.1.1.4.1: mostrarMensajeCorreoUsado()
    private void mostrarMensajeCorreoUsado(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        session.setAttribute("flash_status", "ERROR");
        session.setAttribute("flash_message", "El correo electrónico ya está registrado.");
        resp.sendRedirect(req.getContextPath() + "/jsp/registrar.jsp");
    }

    // 2.1.1.1.5.1: mostrarMensajeDatosInvalidos()
    private void mostrarMensajeDatosInvalidos(HttpServletRequest req, HttpServletResponse resp, String mensaje) throws IOException {
        HttpSession session = req.getSession();
        session.setAttribute("flash_status", "ERROR");
        session.setAttribute("flash_message", mensaje);
        resp.sendRedirect(req.getContextPath() + "/jsp/registrar.jsp");
    }
}