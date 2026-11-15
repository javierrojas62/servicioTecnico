package controlador;

import entidad.Usuario;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import sesion.UsuarioFacade;

/**
 * Controlador (MVC): atiende todas las URLs de la aplicación,
 * usa los Session Beans (Facades) y reenvía a las vistas JSP.
 */
@WebServlet(name = "Manejador",
        loadOnStartup = 1,
        urlPatterns = {"/Login", "/Logout"})
public class Manejador extends HttpServlet {

    @EJB
    private UsuarioFacade usuarioF;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String pathUsuario = request.getServletPath();
        String url = null;

        switch (pathUsuario) {

            // ============================ SESIÓN ============================
            case "/Login": {
                String usuario = request.getParameter("usuario");
                String clave = request.getParameter("clave");
                Usuario u = (usuario == null || clave == null) ? null : usuarioF.login(usuario, clave);

                if (u == null) {
                    request.setAttribute("error", "Usuario o contraseña incorrectos.");
                    url = "/login.jsp";
                    break;
                }
                // Nuevo id de sesión al iniciar sesión (evita el robo de sesión)
                request.changeSessionId();
                request.getSession().setAttribute("usuario", u);    // ← scope session
                response.sendRedirect(request.getContextPath() + "/index.jsp");
                return;
            }

            case "/Logout": {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            default: {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
        }

        request.getRequestDispatcher(url).forward(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
