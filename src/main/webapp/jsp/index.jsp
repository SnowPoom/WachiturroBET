<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WachiturroBet - Home</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/styles.css">
</head>
<body>

    <header class="navbar">
        <div class="container nav-content">
            <a href="index.jsp" class="logo">
                <div class="logo-icon"> <img src="${pageContext.request.contextPath}/jsp/image.png" alt="icon"></div>
                <span>WachiturroBet</span>
            </a>
            <div class="flex-center gap-2">
                <a href="login.jsp" class="btn btn-outline">Ingresar</a>
                <a href="register.jsp" class="btn btn-primary">Registro</a>
            </div>
        </div>
    </header>

    <div class="container" style="padding-top: 2rem; padding-bottom: 2rem;">
        <div class="text-center mb-8">
            <h1 class="text-4xl">Apuestas en Vivo</h1>
            <p class="card-desc">Únete y comienza a jugar</p>
        </div>

        <div class="card">
            <div class="card-content">
                <a href="jsp/billetera.jsp" class="btn btn-primary">Ir a mi Billetera</a>
            </div>
        </div>
    </div>
</body>
</html>