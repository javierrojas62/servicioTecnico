<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Servicio Técnico de PC</title>
</head>
<body>
    <%@include file="/WEB-INF/vista/plantilla/encabezado.jspf" %>

    <h1>Bienvenido, ${sessionScope.usuario.nombre}</h1>

    <%-- Menú común: admin y empleado --%>
    <h3>Atención</h3>
    <ul>
        <li><a href="ListarClientes">Clientes</a></li>
        <li><a href="ListarOrdenes">Órdenes de reparación</a></li>
        <li><a href="ListarItems">Ítems y precios</a></li>
    </ul>

    <%-- Menú solo para el administrador --%>
    <c:if test="${sessionScope.usuario.rol == 'ADMIN'}">
        <h3>Administración</h3>
        <ul>
            <li><a href="ListarUsuarios">Usuarios</a></li>
            <li><a href="NuevoItem">Agregar ítem</a></li>
        </ul>
    </c:if>
</body>
</html>
