<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Registro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/styles.css">
</head>
<body>
    <div class="container" style="padding-top: 2rem;">
        <h2>Registro</h2>
        <form method="post" action="#">
            <label>Nombre</label>
            <input type="text" name="nombre" required />
            <label>Apellido</label>
            <input type="text" name="apellido" required />
            <label>Correo</label>
            <input type="email" name="correo" required />
            <label>Clave</label>
            <input type="password" name="clave" required />
            <button type="submit">Registrarse</button>
        </form>
    </div>
</body>
</html>