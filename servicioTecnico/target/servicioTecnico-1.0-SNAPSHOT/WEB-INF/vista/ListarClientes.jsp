<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Clientes</title>
</head>
<body>
    <%@include file="/WEB-INF/vista/plantilla/encabezado.jspf" %>

    <h1>Clientes</h1>
    <a href="NuevoCliente">Registrar cliente</a>
    <br><br>

    <form action="ListarClientes" method="get">
        <input type="text" name="q" value="${q}" placeholder="Apellido, nombre, teléfono o N°">
        <button type="submit">Buscar</button>
        <c:if test="${not empty q}"><a href="ListarClientes">Ver todos</a></c:if>
    </form>
    <br>

    <c:choose>
        <c:when test="${empty clientes}">
            <p>No se encontraron clientes.</p>
        </c:when>
        <c:otherwise>
            <table border="1">
                <tr><th>N° cliente</th><th>Apellido y nombre</th><th>Teléfono</th><th>Correo</th><th>Alta</th><th>Acciones</th></tr>
                <c:forEach var="c" items="${clientes}">
                    <tr>
                        <td>CLI-<fmt:formatNumber value="${c.idcliente}" minIntegerDigits="4" groupingUsed="false"/></td>
                        <td>${c.apellido}, ${c.nombre}</td>
                        <td>${c.telefono}</td>
                        <td>${empty c.correo ? '-' : c.correo}</td>
                        <td><fmt:formatDate value="${c.fechaAlta}" pattern="dd/MM/yyyy"/></td>
                        <td>
                            <a href="EditarCliente?id=${c.idcliente}">Editar</a> |
                            <a href="ListarOrdenes?cliente=${c.idcliente}">Ver órdenes</a> |
                            <a href="NuevaOrden?cliente=${c.idcliente}">Nueva orden</a>
                            <c:if test="${sessionScope.usuario.rol == 'ADMIN'}">
                                <form action="EliminarCliente" method="post" style="display:inline"
                                      onsubmit="return confirm('¿Eliminar este cliente?');">
                                    <input type="hidden" name="id" value="${c.idcliente}">
                                    <button type="submit">Eliminar</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
            </table>
        </c:otherwise>
    </c:choose>
</body>
</html>
