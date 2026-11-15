<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Iniciar sesión - Servicio Técnico</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body class="login">
    <div class="tarjeta">
        <h1>Servicio Técnico de PC</h1>
        <p class="sub">Ingresá con tu usuario</p>

        <c:if test="${not empty error}">
            <p class="error">${error}</p>
        </c:if>

        <form action="Login" method="post">
            <label>Usuario</label>
            <input type="text" name="usuario" required autofocus>

            <label>Contraseña</label>
            <input type="password" name="clave" required>

            <button type="submit">Ingresar</button>
        </form>
    </div>
</body>
</html>
