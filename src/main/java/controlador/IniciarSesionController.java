package controlador;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

import modelo.dao.JPAUtil;
import modelo.dao.jpa.UsuarioJPADAO;
import modelo.entidades.Usuario;
import modelo.entidades.Administrador;
import modelo.entidades.UsuarioRegistrado;

@WebServlet("/IniciarSesionController")
public class IniciarSesionController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // ELIMINADO: No declares el DAO ni el EntityManager aquí como atributos de clase.
    // private UsuarioJPADAO usuarioDAO;

    // ELIMINADO: No uses init() para esto.
    // @Override
    // public void init() throws ServletException { ... }

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
                this.entrar(req, resp);
                break;
            case "ingresarCredenciales":
                this.ingresarCredenciales(req, resp);
                break;
            default:
                this.entrar(req, resp);
                break;
        }
    }

    public void entrar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        this.mostrarFormulario(req, resp);
    }

    public void ingresarCredenciales(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String correo = req.getParameter("email");
        String clave = req.getParameter("password");

        if(correo == null || clave == null || correo.isEmpty() || clave.isEmpty()){
             this.mostrarCredencialesIncorrectas(req, resp);
             return;
        }

        // --- CORRECCIÓN AQUI ---
        // 1. Obtenemos el EntityManager NUEVO para esta petición específica
        EntityManager em = JPAUtil.getEntityManager();
        
        try {
            // 2. Instanciamos el DAO pasándole este EM fresco
            UsuarioJPADAO usuarioDAO = new UsuarioJPADAO(em);
            
            // 3. Autenticamos
            Usuario usuario = usuarioDAO.autenticar(correo, clave);

            if (usuario != null) {
                HttpSession session = req.getSession();

                // Si es UsuarioRegistrado, obtenemos la entidad manejada por JPA (para poder leer relaciones)
                if (usuario instanceof UsuarioRegistrado) {
                    UsuarioRegistrado ur = em.find(UsuarioRegistrado.class, usuario.getId());
                    session.setAttribute("currentUser", ur);
                    session.setAttribute("currentUserId", ur.getId());
                    // Mantener compatibilidad con controladores que aún usan "usuario"
                    session.setAttribute("usuario", ur);

                    // Cargar saldo desde BD y guardarlo en sesión
                    try {
                        Double saldo = em.createQuery("SELECT b.saldo FROM Billetera b WHERE b.usuario.id = :uid", Double.class)
                                        .setParameter("uid", ur.getId())
                                        .getSingleResult();
                        session.setAttribute("currentUserSaldo", saldo != null ? saldo : 0.0);
                    } catch (Exception ex) {
                        // Si ocurre algún error (no tiene billetera, etc), fallback a 0.0
                        session.setAttribute("currentUserSaldo", 0.0);
                    }

                    // Redirigir al usuario registrado a la lista de eventos
                    resp.sendRedirect("ListarEventosController?ruta=listar"); 
                    return;

                } else if (usuario instanceof Administrador) {
                    // Guardar admin en sesión (usamos atributos diferentes en los JSPs cuando corresponde)
                    session.setAttribute("currentUser", usuario);
                    session.setAttribute("currentUserId", usuario.getId());
                    session.setAttribute("usuario", usuario);
                    resp.sendRedirect("gestionarEventos");
                    return;
                } else {
                    // Guardar usuario genérico
                    session.setAttribute("currentUser", usuario);
                    session.setAttribute("usuario", usuario);
                    resp.sendRedirect("index.jsp");
                    return;
                }
            } else {
                this.mostrarCredencialesIncorrectas(req, resp);
            }
        } catch (Exception e) {
            e.printStackTrace();
            this.mostrarCredencialesIncorrectas(req, resp);
        } finally {
            // 4. IMPORTANTE: Cerrar el EntityManager al final de la petición
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    public void mostrarCredencialesIncorrectas(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("error", "Credenciales incorrectas. Inténtalo de nuevo.");
        this.mostrarFormulario(req, resp);
    }

    public void mostrarFormulario(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/jsp/login.jsp").forward(req, resp);
    }
}