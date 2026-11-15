<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Ítems y precios</title>
</head>
<body>
    <%@include file="/WEB-INF/vista/plantilla/encabezado.jspf" %>

    <h1>Ítems y precios</h1>
    <c:set var="esAdmin" value="${sessionScope.usuario.rol == 'ADMIN'}"/>

    <c:if test="${esAdmin}">
        <a href="NuevoItem">Agregar ítem</a><br><br>
    </c:if>

    <table border="1">
        <tr>
            <th>Descripción</th><th>Precio</th>
            <c:if test="${esAdmin}"><th>Acciones</th></c:if>
        </tr>
        <c:forEach var="i" items="${items}">
            <tr>
                <td>${i.descripcion}</td>
                <td style="text-align:right">$ <fmt:formatNumber value="${i.precio}" minFractionDigits="2" maxFractionDigits="2"/></td>
                <c:if test="${esAdmin}">
                    <td>
                        <a href="EditarItem?id=${i.iditem}">Editar</a>
                        <form action="EliminarItem" method="post" style="display:inline"
                              onsubmit="return confirm('¿Eliminar este ítem?');">
                            <input type="hidden" name="id" value="${i.iditem}">
                            <button type="submit">Eliminar</button>
                        </form>
                    </td>
                </c:if>
            </tr>
        </c:forEach>
    </table>
</body>
</html>
