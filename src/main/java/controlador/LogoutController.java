package controlador;

import jakarta.persistence.EntityManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import modelo.dao.JPAUtil;
import modelo.dao.jpa.UsuarioJPADAO;

import java.io.IOException;

@WebServlet("/LogoutController")
public class LogoutController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        cerrarSesion(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        cerrarSesion(req, resp);
    }
    
    private void ruteador(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		cerrarSesion(req, resp);
	}

    public void cerrarSesion(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            Integer userId = (Integer) session.getAttribute("currentUserId");
            EntityManager em = null;
            if (userId != null) {
                em = JPAUtil.getEntityManager();
                UsuarioJPADAO dao = new UsuarioJPADAO(em);
                dao.finalizarSesion(userId);
            }

            session.invalidate();
        }

        resp.sendRedirect(req.getContextPath() + "/jsp/login.jsp");
    }
}
