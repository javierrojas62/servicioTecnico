<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Ítem</title>
</head>
<body>
    <%@include file="/WEB-INF/vista/plantilla/encabezado.jspf" %>

    <h1>${empty item ? 'Agregar ítem' : 'Editar ítem'}</h1>

    <form action="GuardarItem" method="post">
        <c:if test="${not empty item}">
            <input type="hidden" name="id" value="${item.iditem}">
        </c:if>

        <label>Descripción:</label>
        <input type="text" name="descripcion" maxlength="80" required value="${item.descripcion}"><br><br>

        <label>Precio ($):</label>
        <input type="number" name="precio" step="0.01" min="0" required value="${item.precio}"><br><br>

        <button type="submit">Guardar</button>
    </form>
    <br><a href="ListarItems">Volver al listado</a>
</body>
</html>
