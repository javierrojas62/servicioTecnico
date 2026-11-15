<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Usuario</title>
</head>
<body>
    <%@include file="/WEB-INF/vista/plantilla/encabezado.jspf" %>

    <%-- Mismo formulario para crear (sin u) y editar (con u) --%>
    <h1>${empty u ? 'Agregar usuario' : 'Editar usuario'}</h1>

    <form action="GuardarUsuario" method="post">
        <c:if test="${not empty u}">
            <input type="hidden" name="id" value="${u.idusuario}">
        </c:if>

        <label>Nombre y apellido:</label>
        <input type="text" name="nombre" maxlength="60" required value="${u.nombre}"><br><br>

        <label>Usuario:</label>
        <input type="text" name="usuario" maxlength="30" required value="${u.usuario}"><br><br>

        <label>Contraseña:</label>
        <c:choose>
            <c:when test="${empty u}">
                <input type="password" name="clave" minlength="6" required><br><br>
            </c:when>
            <c:otherwise>
                <input type="password" name="clave" minlength="6"
                       placeholder="Dejar vacío para no cambiarla"><br><br>
            </c:otherwise>
        </c:choose>

        <label>Rol:</label>
        <select name="rol" required>
            <option value="EMPLEADO" <c:if test="${u.rol == 'EMPLEADO'}">selected</c:if>>Empleado</option>
            <option value="ADMIN" <c:if test="${u.rol == 'ADMIN'}">selected</c:if>>Administrador</option>
        </select><br><br>

        <button type="submit">Guardar</button>
    </form>
    <br><a href="ListarUsuarios">Volver al listado</a>
</body>
</html>
