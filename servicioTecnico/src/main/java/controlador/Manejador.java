package controlador;

import entidad.Cliente;
import entidad.Item;
import entidad.Orden;
import entidad.Usuario;
import jakarta.ejb.EJB;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;
import sesion.ClienteFacade;
import sesion.ItemFacade;
import sesion.OrdenFacade;
import sesion.UsuarioFacade;

/**
 * Controlador (MVC): atiende todas las URLs de la aplicación,
 * usa los Session Beans (Facades) y reenvía a las vistas JSP.
 * El control de sesión y de roles lo hace antes el FiltroAcceso.
 */
@WebServlet(name = "Manejador",
        loadOnStartup = 1,
        urlPatterns = {
            // Sesión
            "/Login", "/Logout",
            // Usuarios (solo ADMIN)
            "/ListarUsuarios", "/NuevoUsuario", "/EditarUsuario", "/GuardarUsuario", "/CambiarEstadoUsuario",
            // Ítems (listar: todos; el resto: solo ADMIN)
            "/ListarItems", "/NuevoItem", "/EditarItem", "/GuardarItem", "/EliminarItem",
            // Clientes (eliminar: solo ADMIN)
            "/ListarClientes", "/NuevoCliente", "/EditarCliente", "/GuardarCliente", "/EliminarCliente",
            // Órdenes de reparación
            "/ListarOrdenes", "/NuevaOrden", "/GuardarOrden", "/VerOrden",
            "/AgregarItemOrden", "/QuitarItemOrden", "/CambiarEstadoOrden"})
public class Manejador extends HttpServlet {

    @EJB
    private UsuarioFacade usuarioF;

    @EJB
    private ItemFacade itemF;

    @EJB
    private ClienteFacade clienteF;

    @EJB
    private OrdenFacade ordenF;

    // Estados posibles de una orden (los mismos que el ENUM de la base)
    private static final Set<String> ESTADOS =
            Set.of("INGRESADO", "EN_DIAGNOSTICO", "EN_REPARACION", "LISTO", "ENTREGADO");

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String pathUsuario = request.getServletPath();
        HttpSession session = request.getSession();
        String url = null;

        switch (pathUsuario) {

            // ============================ SESIÓN ============================
            case "/Login": {
                String usuario = request.getParameter("usuario");
                String clave = request.getParameter("clave");
                Usuario u = (usuario == null || clave == null) ? null : usuarioF.login(usuario, clave);

                if (u == null) {
                    request.setAttribute("error", "Usuario o contraseña incorrectos, o usuario inactivo.");
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
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/login.jsp");
                return;
            }

            // =========================== USUARIOS ===========================
            case "/ListarUsuarios": {
                request.setAttribute("usuarios", usuarioF.listarOrdenados());
                url = "/WEB-INF/vista/ListarUsuarios.jsp";
                break;
            }

            case "/NuevoUsuario": {
                url = "/WEB-INF/vista/FormUsuario.jsp";                       // formulario vacío
                break;
            }

            case "/EditarUsuario": {
                Integer id = Integer.valueOf(request.getParameter("id"));
                request.setAttribute("u", usuarioF.find(id));                 // formulario con datos
                url = "/WEB-INF/vista/FormUsuario.jsp";
                break;
            }

            case "/GuardarUsuario": {
                String idParam = request.getParameter("id");
                Integer id = (idParam == null || idParam.isEmpty()) ? null : Integer.valueOf(idParam);
                String nombre = request.getParameter("nombre").trim();
                String usuario = request.getParameter("usuario").trim();
                String clave = request.getParameter("clave");
                String rol = request.getParameter("rol");

                // Validación: el nombre de usuario no se puede repetir
                if (usuarioF.existeUsuario(usuario, id)) {
                    session.setAttribute("error", "Ya existe un usuario con el nombre '" + usuario + "'.");
                    response.sendRedirect(request.getContextPath() + "/ListarUsuarios");
                    return;
                }

                if (id == null) {
                    Usuario u = new Usuario();                                // sin id → nuevo
                    u.setNombre(nombre);
                    u.setUsuario(usuario);
                    u.setPassword(UsuarioFacade.hash(clave));                 // se guarda el hash
                    u.setRol(rol);
                    u.setActivo(true);
                    usuarioF.create(u);                                       // INSERT
                    session.setAttribute("mensaje", "Usuario creado.");
                } else {
                    Usuario u = usuarioF.find(id);                            // con id → existente
                    if (u != null) {
                        u.setNombre(nombre);
                        u.setUsuario(usuario);
                        if (clave != null && !clave.isEmpty()) {              // clave vacía = no se cambia
                            u.setPassword(UsuarioFacade.hash(clave));
                        }
                        u.setRol(rol);
                        usuarioF.edit(u);                                     // UPDATE
                        session.setAttribute("mensaje", "Usuario actualizado.");
                    }
                }
                response.sendRedirect(request.getContextPath() + "/ListarUsuarios");
                return;
            }

            case "/CambiarEstadoUsuario": {
                // Baja lógica: el usuario no se borra (tiene órdenes asociadas), se desactiva
                Integer id = Integer.valueOf(request.getParameter("id"));
                Usuario logueado = (Usuario) session.getAttribute("usuario");
                if (id.equals(logueado.getIdusuario())) {
                    session.setAttribute("error", "No podés desactivar tu propio usuario.");
                } else {
                    Usuario u = usuarioF.find(id);
                    if (u != null) {
                        u.setActivo(!u.getActivo());
                        usuarioF.edit(u);
                        session.setAttribute("mensaje",
                                u.getActivo() ? "Usuario activado." : "Usuario desactivado.");
                    }
                }
                response.sendRedirect(request.getContextPath() + "/ListarUsuarios");
                return;
            }

            // ============================= ÍTEMS =============================
            case "/ListarItems": {
                request.setAttribute("items", itemF.listarOrdenados());
                url = "/WEB-INF/vista/ListarItems.jsp";
                break;
            }

            case "/NuevoItem": {
                url = "/WEB-INF/vista/FormItem.jsp";
                break;
            }

            case "/EditarItem": {
                Integer id = Integer.valueOf(request.getParameter("id"));
                request.setAttribute("item", itemF.find(id));
                url = "/WEB-INF/vista/FormItem.jsp";
                break;
            }

            case "/GuardarItem": {
                String idParam = request.getParameter("id");
                String descripcion = request.getParameter("descripcion").trim();
                BigDecimal precio = new BigDecimal(request.getParameter("precio"));

                if (idParam == null || idParam.isEmpty()) {
                    Item i = new Item();
                    i.setDescripcion(descripcion);
                    i.setPrecio(precio);
                    itemF.create(i);                                          // INSERT
                    session.setAttribute("mensaje", "Ítem creado.");
                } else {
                    Item i = itemF.find(Integer.valueOf(idParam));
                    if (i != null) {
                        i.setDescripcion(descripcion);
                        i.setPrecio(precio);                                  // las órdenes viejas no cambian
                        itemF.edit(i);                                        // UPDATE
                        session.setAttribute("mensaje", "Ítem actualizado.");
                    }
                }
                response.sendRedirect(request.getContextPath() + "/ListarItems");
                return;
            }

            case "/EliminarItem": {
                Integer id = Integer.valueOf(request.getParameter("id"));
                if (itemF.cantidadUsos(id) > 0) {
                    session.setAttribute("error", "No se puede eliminar: el ítem ya se usó en órdenes.");
                } else {
                    Item i = itemF.find(id);
                    if (i != null) {
                        itemF.remove(i);                                      // DELETE
                        session.setAttribute("mensaje", "Ítem eliminado.");
                    }
                }
                response.sendRedirect(request.getContextPath() + "/ListarItems");
                return;
            }

            // =========================== CLIENTES ===========================
            case "/ListarClientes": {
                String q = request.getParameter("q");                         // texto de búsqueda
                request.setAttribute("q", q);
                request.setAttribute("clientes", clienteF.buscar(q));
                url = "/WEB-INF/vista/ListarClientes.jsp";
                break;
            }

            case "/NuevoCliente": {
                url = "/WEB-INF/vista/FormCliente.jsp";
                break;
            }

            case "/EditarCliente": {
                Integer id = Integer.valueOf(request.getParameter("id"));
                request.setAttribute("cliente", clienteF.find(id));
                url = "/WEB-INF/vista/FormCliente.jsp";
                break;
            }

            case "/GuardarCliente": {
                String idParam = request.getParameter("id");
                String correo = request.getParameter("correo").trim();
                Cliente c;
                if (idParam == null || idParam.isEmpty()) {
                    c = new Cliente();
                    c.setFechaAlta(new Date());          // el número de cliente lo genera MySQL (AUTO_INCREMENT)
                } else {
                    c = clienteF.find(Integer.valueOf(idParam));
                }
                c.setNombre(request.getParameter("nombre").trim());
                c.setApellido(request.getParameter("apellido").trim());
                c.setCorreo(correo.isEmpty() ? null : correo);
                c.setTelefono(request.getParameter("telefono").trim());

                if (c.getIdcliente() == null) {
                    clienteF.create(c);                  // INSERT → después del commit ya tiene su número
                    session.setAttribute("mensaje", "Cliente registrado con el número " + c.getIdcliente() + ".");
                } else {
                    clienteF.edit(c);                    // UPDATE
                    session.setAttribute("mensaje", "Cliente actualizado.");
                }
                response.sendRedirect(request.getContextPath() + "/ListarClientes");
                return;
            }

            case "/EliminarCliente": {
                Integer id = Integer.valueOf(request.getParameter("id"));
                if (clienteF.cantidadOrdenes(id) > 0) {
                    session.setAttribute("error", "No se puede eliminar: el cliente tiene órdenes registradas.");
                } else {
                    Cliente c = clienteF.find(id);
                    if (c != null) {
                        clienteF.remove(c);              // DELETE
                        session.setAttribute("mensaje", "Cliente eliminado.");
                    }
                }
                response.sendRedirect(request.getContextPath() + "/ListarClientes");
                return;
            }

            // ============================ ÓRDENES ============================
            case "/ListarOrdenes": {
                String estado = request.getParameter("estado");
                String clienteParam = request.getParameter("cliente");
                if (estado != null && !ESTADOS.contains(estado)) {
                    estado = null;                       // "Todos" o un valor inválido = sin filtro
                }
                Integer idcliente = (clienteParam == null || clienteParam.isEmpty())
                        ? null : Integer.valueOf(clienteParam);
                request.setAttribute("estado", estado);
                request.setAttribute("filtroCliente", idcliente == null ? null : clienteF.find(idcliente));
                request.setAttribute("ordenes", ordenF.listar(estado, idcliente));
                request.setAttribute("resumen", ordenF.contarPorEstado());
                url = "/WEB-INF/vista/ListarOrdenes.jsp";
                break;
            }

            case "/NuevaOrden": {
                request.setAttribute("clientes", clienteF.buscar(null));
                request.setAttribute("clienteElegido", request.getParameter("cliente"));  // viene desde Clientes
                url = "/WEB-INF/vista/FormOrden.jsp";
                break;
            }

            case "/GuardarOrden": {
                Orden o = new Orden();
                o.setClienteIdcliente(clienteF.find(Integer.valueOf(request.getParameter("cliente"))));
                Usuario logueado = (Usuario) session.getAttribute("usuario");
                o.setUsuarioIdusuario(usuarioF.find(logueado.getIdusuario()));     // quien la registra
                o.setEquipo(request.getParameter("equipo").trim());
                o.setFalla(request.getParameter("falla").trim());
                String obs = request.getParameter("observaciones");
                o.setObservaciones(obs == null || obs.isBlank() ? null : obs.trim());
                o.setEstado("INGRESADO");
                o.setFechaIngreso(new Date());
                ordenF.create(o);                        // INSERT
                session.setAttribute("mensaje", "Orden N° " + o.getIdorden() + " creada. Agregá los ítems.");
                response.sendRedirect(request.getContextPath() + "/VerOrden?id=" + o.getIdorden());
                return;
            }

            case "/VerOrden": {
                Integer id = Integer.valueOf(request.getParameter("id"));
                Orden o = ordenF.buscarConItems(id);
                if (o == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
                request.setAttribute("orden", o);
                request.setAttribute("items", itemF.listarOrdenados());              // para agregar ítems
                url = "/WEB-INF/vista/VerOrden.jsp";
                break;
            }

            case "/AgregarItemOrden": {
                Integer id = Integer.valueOf(request.getParameter("orden"));
                Integer iditem = Integer.valueOf(request.getParameter("item"));
                int cantidad = Math.max(1, Integer.parseInt(request.getParameter("cantidad")));
                if (!ordenF.agregarItem(id, iditem, cantidad)) {
                    session.setAttribute("error", "No se puede modificar una orden entregada.");
                }
                response.sendRedirect(request.getContextPath() + "/VerOrden?id=" + id);
                return;
            }

            case "/QuitarItemOrden": {
                Integer id = Integer.valueOf(request.getParameter("orden"));
                Integer iditem = Integer.valueOf(request.getParameter("item"));
                if (!ordenF.quitarItem(id, iditem)) {
                    session.setAttribute("error", "No se puede modificar una orden entregada.");
                }
                response.sendRedirect(request.getContextPath() + "/VerOrden?id=" + id);
                return;
            }

            case "/CambiarEstadoOrden": {
                Integer id = Integer.valueOf(request.getParameter("orden"));
                String estado = request.getParameter("estado");
                String obs = request.getParameter("observaciones");
                if (ESTADOS.contains(estado)) {
                    ordenF.cambiarEstado(id, estado, obs == null || obs.isBlank() ? null : obs.trim());
                    session.setAttribute("mensaje", "Orden actualizada.");
                }
                response.sendRedirect(request.getContextPath() + "/VerOrden?id=" + id);
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
