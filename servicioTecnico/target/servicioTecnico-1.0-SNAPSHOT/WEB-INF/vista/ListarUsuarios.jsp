<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Usuarios</title>
</head>
<body>
    <%@include file="/WEB-INF/vista/plantilla/encabezado.jspf" %>

    <h1>Usuarios</h1>
    <a href="NuevoUsuario">Agregar usuario</a>
    <br><br>
    <table border="1">
        <tr><th>Nombre</th><th>Usuario</th><th>Rol</th><th>Estado</th><th>Acciones</th></tr>
        <c:forEach var="u" items="${usuarios}">
            <tr>
                <td>${u.nombre}</td>
                <td>${u.usuario}</td>
                <td>${u.rol}</td>
                <td>${u.activo ? 'Activo' : 'Inactivo'}</td>
                <td>
                    <a href="EditarUsuario?id=${u.idusuario}">Editar</a>
                    <form action="CambiarEstadoUsuario" method="post" style="display:inline">
                        <input type="hidden" name="id" value="${u.idusuario}">
                        <button type="submit">${u.activo ? 'Desactivar' : 'Activar'}</button>
                    </form>
                </td>
            </tr>
        </c:forEach>
    </table>
</body>
</html>
