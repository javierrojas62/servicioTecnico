<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Orden N° ${orden.idorden}</title>
</head>
<body>
    <%@include file="/WEB-INF/vista/plantilla/encabezado.jspf" %>

    <h1>Orden N° ${orden.idorden}</h1>
    <c:set var="cerrada" value="${orden.estado == 'ENTREGADO'}"/>

    <%-- Datos de la orden --%>
    <table border="1">
        <tr><th>Cliente</th>
            <td>${orden.clienteIdcliente.apellido}, ${orden.clienteIdcliente.nombre}
                (N° ${orden.clienteIdcliente.idcliente}) · Tel: ${orden.clienteIdcliente.telefono}</td></tr>
        <tr><th>Equipo</th><td>${orden.equipo}</td></tr>
        <tr><th>Falla</th><td>${orden.falla}</td></tr>
        <tr><th>Ingreso</th><td><fmt:formatDate value="${orden.fechaIngreso}" pattern="dd/MM/yyyy HH:mm"/></td></tr>
        <tr><th>Registró</th><td>${orden.usuarioIdusuario.nombre}</td></tr>
        <tr><th>Estado</th><td><b>${fn:replace(orden.estado, '_', ' ')}</b></td></tr>
        <tr><th>Observaciones</th><td>${empty orden.observaciones ? '-' : orden.observaciones}</td></tr>
    </table>

    <%-- Cambiar estado y observaciones --%>
    <h3>Actualizar estado</h3>
    <form action="CambiarEstadoOrden" method="post">
        <input type="hidden" name="orden" value="${orden.idorden}">
        <select name="estado">
            <c:forTokens var="e" items="INGRESADO,EN_DIAGNOSTICO,EN_REPARACION,LISTO,ENTREGADO" delims=",">
                <option value="${e}" <c:if test="${e == orden.estado}">selected</c:if>>${fn:replace(e, '_', ' ')}</option>
            </c:forTokens>
        </select>
        <input type="text" name="observaciones" maxlength="255" size="50"
               value="${orden.observaciones}" placeholder="Observaciones">
        <button type="submit">Guardar</button>
    </form>

    <%-- Ítems de la orden --%>
    <h3>Servicios y repuestos</h3>
    <c:choose>
        <c:when test="${empty orden.ordenItemCollection}">
            <p>Todavía no se agregaron ítems.</p>
        </c:when>
        <c:otherwise>
            <table border="1">
                <tr><th>Ítem</th><th>Cantidad</th><th>Precio unitario</th><th>Subtotal</th>
                    <c:if test="${not cerrada}"><th></th></c:if></tr>
                <c:forEach var="oi" items="${orden.ordenItemCollection}">
                    <tr>
                        <td>${oi.item.descripcion}</td>
                        <td style="text-align:center">${oi.cantidad}</td>
                        <td style="text-align:right">$ <fmt:formatNumber value="${oi.precioUnitario}" minFractionDigits="2" maxFractionDigits="2"/></td>
                        <td style="text-align:right">$ <fmt:formatNumber value="${oi.subtotal}" minFractionDigits="2" maxFractionDigits="2"/></td>
                        <c:if test="${not cerrada}">
                            <td>
                                <form action="QuitarItemOrden" method="post" style="display:inline">
                                    <input type="hidden" name="orden" value="${orden.idorden}">
                                    <input type="hidden" name="item" value="${oi.item.iditem}">
                                    <button type="submit">Quitar</button>
                                </form>
                            </td>
                        </c:if>
                    </tr>
                </c:forEach>
                <tr>
                    <td colspan="3"><b>TOTAL</b></td>
                    <td style="text-align:right"><b>$ <fmt:formatNumber value="${orden.total}" minFractionDigits="2" maxFractionDigits="2"/></b></td>
                    <c:if test="${not cerrada}"><td></td></c:if>
                </tr>
            </table>
        </c:otherwise>
    </c:choose>

    <%-- Agregar ítem (solo si la orden no fue entregada) --%>
    <c:if test="${not cerrada}">
        <br>
        <form action="AgregarItemOrden" method="post">
            <input type="hidden" name="orden" value="${orden.idorden}">
            <select name="item" required>
                <c:forEach var="i" items="${items}">
                    <option value="${i.iditem}">
                        ${i.descripcion} - $ <fmt:formatNumber value="${i.precio}" minFractionDigits="2" maxFractionDigits="2"/>
                    </option>
                </c:forEach>
            </select>
            <input type="number" name="cantidad" value="1" min="1" max="99" style="width:4em">
            <button type="submit">Agregar</button>
        </form>
    </c:if>
    <c:if test="${cerrada}">
        <p><i>La orden fue entregada: ya no se pueden modificar sus ítems.</i></p>
    </c:if>

    <br><a href="ListarOrdenes">Volver al listado</a>
</body>
</html>
