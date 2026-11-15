package seguridad;

import entidad.Usuario;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;

/**
 * Filtro de seguridad: se ejecuta ANTES de cada petición.
 * 1) Si no hay sesión iniciada, manda al login.
 * 2) Si la URL es solo para administradores y el usuario es EMPLEADO, la bloquea.
 */
@WebFilter("/*")
public class FiltroAcceso implements Filter {

    // URLs que se pueden visitar sin iniciar sesión
    private static final Set<String> PUBLICAS = Set.of("/login.jsp", "/Login");

    // URLs exclusivas del rol ADMIN (se van agregando a medida que se crean)
    private static final Set<String> SOLO_ADMIN = Set.of(
            "/ListarUsuarios", "/NuevoUsuario", "/EditarUsuario", "/GuardarUsuario", "/EliminarUsuario",
            "/NuevoItem", "/EditarItem", "/GuardarItem", "/EliminarItem");

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        String path = request.getServletPath();

        // Recursos públicos y estáticos (estilos, imágenes): pasan sin control
        if (PUBLICAS.contains(path) || path.startsWith("/css/") || path.startsWith("/img/")) {
            chain.doFilter(req, res);
            return;
        }

        // ¿Hay usuario en la sesión?
        HttpSession session = request.getSession(false);
        Usuario u = (session == null) ? null : (Usuario) session.getAttribute("usuario");
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // ¿La URL es de administrador y el usuario no lo es?
        if (SOLO_ADMIN.contains(path) && !"ADMIN".equals(u.getRol())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Acceso solo para administradores");
            return;
        }

        chain.doFilter(req, res);   // todo en orden: sigue hacia el servlet o la JSP
    }
}
