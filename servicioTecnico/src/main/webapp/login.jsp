<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Iniciar sesión - Servicio Técnico</title>
</head>
<body>
    <h1>Servicio Técnico de PC</h1>
    <h2>Iniciar sesión</h2>

    <c:if test="${not empty error}">
        <p style="color:red"><b>${error}</b></p>
    </c:if>

    <form action="Login" method="post">
        <label>Usuario:</label>
        <input type="text" name="usuario" required autofocus><br><br>

        <label>Contraseña:</label>
        <input type="password" name="clave" required><br><br>

        <button type="submit">Ingresar</button>
    </form>
</body>
</html>
