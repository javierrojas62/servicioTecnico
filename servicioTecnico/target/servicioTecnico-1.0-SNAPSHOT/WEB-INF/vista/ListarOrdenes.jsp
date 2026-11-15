<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Órdenes de reparación</title>
</head>
<body>
    <%@include file="/WEB-INF/vista/plantilla/encabezado.jspf" %>

    <h1>Órdenes de reparación</h1>
    <c:if test="${not empty filtroCliente}">
        <p>Cliente: <b>${filtroCliente.apellido}, ${filtroCliente.nombre}</b>
           (<a href="ListarOrdenes">ver todas</a>)</p>
    </c:if>
    <a href="NuevaOrden">Nueva orden</a>
    <br><br>

    <%-- Resumen: cantidad de órdenes por estado (consulta JPQL con GROUP BY) --%>
    <p>
        <c:forEach var="r" items="${resumen}" varStatus="st">
            ${fn:replace(r[0], '_', ' ')}: <b>${r[1]}</b><c:if test="${not st.last}"> · </c:if>
        </c:forEach>
    </p>

    <form action="ListarOrdenes" method="get">
        <c:if test="${not empty filtroCliente}">
            <input type="hidden" name="cliente" value="${filtroCliente.idcliente}">
        </c:if>
        <label>Estado:</label>
        <select name="estado" onchange="this.form.submit()">
            <option value="">Todos</option>
            <c:forTokens var="e" items="INGRESADO,EN_DIAGNOSTICO,EN_REPARACION,LISTO,ENTREGADO" delims=",">
                <option value="${e}" <c:if test="${e == estado}">selected</c:if>>${fn:replace(e, '_', ' ')}</option>
            </c:forTokens>
        </select>
    </form>
    <br>

    <c:choose>
        <c:when test="${empty ordenes}">
            <p>No hay órdenes para mostrar.</p>
        </c:when>
        <c:otherwise>
            <table border="1">
                <tr><th>N°</th><th>Ingreso</th><th>Cliente</th><th>Equipo</th><th>Estado</th><th>Total</th><th></th></tr>
                <c:forEach var="o" items="${ordenes}">
                    <tr>
                        <td>${o.idorden}</td>
                        <td><fmt:formatDate value="${o.fechaIngreso}" pattern="dd/MM/yyyy"/></td>
                        <td>${o.clienteIdcliente.apellido}, ${o.clienteIdcliente.nombre}</td>
                        <td>${o.equipo}</td>
                        <td>${fn:replace(o.estado, '_', ' ')}</td>
                        <td style="text-align:right">$ <fmt:formatNumber value="${o.total}" minFractionDigits="2" maxFractionDigits="2"/></td>
                        <td><a href="VerOrden?id=${o.idorden}">Ver</a></td>
                    </tr>
                </c:forEach>
            </table>
        </c:otherwise>
    </c:choose>
</body>
</html>
