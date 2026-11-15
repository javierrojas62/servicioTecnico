<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Cliente</title>
</head>
<body>
    <%@include file="/WEB-INF/vista/plantilla/encabezado.jspf" %>

    <h1>${empty cliente ? 'Registrar cliente' : 'Editar cliente'}</h1>
    <c:if test="${empty cliente}">
        <p>El número de cliente se asigna automáticamente al guardar.</p>
    </c:if>

    <form action="GuardarCliente" method="post">
        <c:if test="${not empty cliente}">
            <input type="hidden" name="id" value="${cliente.idcliente}">
        </c:if>

        <label>Nombre:</label>
        <input type="text" name="nombre" maxlength="45" required value="${cliente.nombre}"><br><br>

        <label>Apellido:</label>
        <input type="text" name="apellido" maxlength="45" required value="${cliente.apellido}"><br><br>

        <label>Teléfono:</label>
        <input type="tel" name="telefono" maxlength="20" required pattern="[0-9 +()-]{6,20}"
               value="${cliente.telefono}"><br><br>

        <label>Correo (opcional):</label>
        <input type="email" name="correo" maxlength="80" value="${cliente.correo}"><br><br>

        <button type="submit">Guardar</button>
    </form>
    <br><a href="ListarClientes">Volver al listado</a>
</body>
</html>
