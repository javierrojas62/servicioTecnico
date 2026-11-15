<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Servicio Técnico de PC</title>
</head>
<body>
    <h1>Servicio Técnico de PC</h1>
    <p>
        Hola, <b>${sessionScope.usuario.nombre}</b> (${sessionScope.usuario.rol}) |
        <a href="Logout">Cerrar sesión</a>
    </p>
    <hr>

    <%-- Menú común: admin y empleado --%>
    <h3>Atención</h3>
    <ul>
        <li><a href="ListarClientes">Clientes</a></li>
        <li><a href="ListarOrdenes">Órdenes de reparación</a></li>
    </ul>

    <%-- Menú solo para el administrador --%>
    <c:if test="${sessionScope.usuario.rol == 'ADMIN'}">
        <h3>Administración</h3>
        <ul>
            <li><a href="ListarUsuarios">Usuarios</a></li>
            <li><a href="ListarItems">Ítems y precios</a></li>
        </ul>
    </c:if>
</body>
</html>
