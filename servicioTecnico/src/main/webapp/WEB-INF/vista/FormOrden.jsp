<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Nueva orden</title>
</head>
<body>
    <%@include file="/WEB-INF/vista/plantilla/encabezado.jspf" %>

    <h1>Nueva orden de reparación</h1>

    <c:choose>
        <c:when test="${empty clientes}">
            <p>Primero tenés que <a href="NuevoCliente">registrar un cliente</a>.</p>
        </c:when>
        <c:otherwise>
            <form action="GuardarOrden" method="post">
                <label>Cliente:</label>
                <select name="cliente" required>
                    <option value="">-- Elegir --</option>
                    <c:forEach var="c" items="${clientes}">
                        <option value="${c.idcliente}" <c:if test="${c.idcliente == clienteElegido}">selected</c:if>>
                            ${c.apellido}, ${c.nombre} (N° ${c.idcliente})
                        </option>
                    </c:forEach>
                </select>
                <a href="NuevoCliente">Cliente nuevo</a><br><br>

                <label>Equipo:</label>
                <input type="text" name="equipo" maxlength="80" required
                       placeholder="Ej: Notebook Lenovo IdeaPad 3"><br><br>

                <label>Falla reportada:</label><br>
                <textarea name="falla" maxlength="255" rows="3" cols="50" required></textarea><br><br>

                <label>Observaciones (opcional):</label><br>
                <textarea name="observaciones" maxlength="255" rows="2" cols="50"
                          placeholder="Accesorios que deja, contraseña, etc."></textarea><br><br>

                <button type="submit">Crear orden</button>
            </form>
        </c:otherwise>
    </c:choose>
    <br><a href="ListarOrdenes">Volver al listado</a>
</body>
</html>
