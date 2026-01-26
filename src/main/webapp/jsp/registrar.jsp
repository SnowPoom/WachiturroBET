<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WachiturroBet - Registro</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/styles.css">
    <style>
        .register-container {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 1rem;
        }
        .register-card {
            width: 100%;
            max-width: 400px;
        }
        /* Estilos para mensajes flash */
        .result-ok { background: #dcfce7; color: #14532d; padding: 0.75rem; border-radius: 6px; margin-bottom: 1rem; }
        .result-err { background: #fee2e2; color: #7f1d1d; padding: 0.75rem; border-radius: 6px; margin-bottom: 1rem; }
    </style>
</head>
<body>
    <div class="register-container">
        <div class="card register-card">
            
            <%-- BLOQUE DE MENSAJES (Error o Éxito) --%>
            <c:if test="${not empty sessionScope.flash_message}">
                <div class="${sessionScope.flash_status == 'OK' ? 'result-ok' : 'result-err'}">
                    <strong>${sessionScope.flash_status == 'OK' ? '¡Éxito!' : 'Error:'}</strong>
                    <span>${sessionScope.flash_message}</span>
                </div>
                <%-- Limpiar mensajes --%>
                <c:remove var="flash_message" scope="session" />
                <c:remove var="flash_status" scope="session" />
            </c:if>

            <div class="card-header text-center">
                <div class="flex-center mb-4">
                    <div class="logo-icon" style="width: 4rem; height: 4rem; font-size: 1.5rem;">
                        <img src="${pageContext.request.contextPath}/jsp/image.png" alt="icon">
                    </div>
                </div>
                <h1 class="card-title">Crear Cuenta</h1>
                <p class="card-desc">Regístrate para comenzar a apostar</p>
            </div>
            
            <div class="card-content">
                <form action="${pageContext.request.contextPath}/registrarCuenta" method="post">
                    <input type="hidden" name="ruta" value="ingresar" />
                    
                    <div class="form-group">
                        <label class="label" for="name">Nombre</label>
                        <input class="input" id="name" name="nombre" type="text" placeholder="Tu nombre" required>
                    </div>
                    <div class="form-group">
                        <label class="label" for="apellido">Apellido</label>
                        <input class="input" id="apellido" name="apellido" type="text" placeholder="Tu apellido" required>
                    </div>
                    <div class="form-group">
                        <label class="label" for="email">Correo Electrónico</label>
                        <input class="input" id="email" name="correo" type="email" placeholder="tu@email.com" required>
                    </div>
                    <div class="form-group">
                        <label class="label" for="password">Contraseña</label>
                        <input class="input" id="password" name="clave" type="password" placeholder="••••••••" required>
                    </div>
                    
                    <button type="submit" class="btn btn-primary btn-full">Crear Cuenta</button>
                </form>
            </div>
            
            <div class="card-footer text-center">
                <p class="card-desc">¿Ya tienes una cuenta?</p>
                <a href="${pageContext.request.contextPath}/jsp/login.jsp" class="btn btn-outline btn-full">Iniciar Sesión</a>
            </div>
        </div>
    </div>
</body>
</html>