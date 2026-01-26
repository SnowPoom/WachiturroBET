<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WachiturroBet - Login</title>
    <!-- Usar ruta absoluta al contexto para que funcione también cuando la página se sirve vía forward -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/styles.css">
    <style>
        /* Estilo específico solo para centrar el login en pantalla completa */
        .login-container {
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 1rem;
        }
        .login-card {
            width: 100%;
            max-width: 400px;
        }
        /* Estilo extra para el mensaje de error */
        .alert-error {
            background-color: #f8d7da;
            color: #721c24;
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 4px;
            border: 1px solid #f5c6cb;
            text-align: center;
            font-size: 0.9rem;
        }
        .alert-success {
            background-color: #d4edda;
            color: #155724;
            padding: 10px;
            margin-bottom: 15px;
            border-radius: 4px;
            border: 1px solid #c3e6cb;
            text-align: center;
            font-size: 0.9rem;
        }
    </style>
</head>
<body>
    <div class="login-container">
        <div class="card login-card">
            <div class="card-header text-center">
                <div class="flex-center mb-4">
                    <div class="logo-icon" style="width: 4rem; height: 4rem; font-size: 1.5rem;">
                        <img src="${pageContext.request.contextPath}/jsp/image.png" alt="icon" style="max-width:100%; height:auto;">
                    </div>
                </div>
                <h1 class="card-title">Iniciar Sesión</h1>
                <p class="card-desc">Ingresa a tu cuenta para comenzar</p>
            </div>
            
            <div class="card-content">
                <!-- Mostrar mensaje flash guardado en sesión (RegistroController y otros controladores usan flash_status/flash_message) -->
                <c:if test="${not empty sessionScope.flash_message}">
                    <c:choose>
                        <c:when test="${sessionScope.flash_status == 'OK'}">
                            <div class="alert-success">
                                ${sessionScope.flash_message}
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="alert-error">
                                ${sessionScope.flash_message}
                            </div>
                        </c:otherwise>
                    </c:choose>
                    <c:remove var="flash_message" scope="session" />
                    <c:remove var="flash_status" scope="session" />
                    <c:remove var="flash_operacion" scope="session" />
                </c:if>

                <% if(request.getAttribute("error") != null) { %>
                    <div class="alert-error">
                        <%= request.getAttribute("error") %>
                    </div>
                <% } %>

                <form action="${pageContext.request.contextPath}/IniciarSesionController" method="POST">
				<input type="hidden" name="ruta" value="ingresarCredenciales">	
                    <div class="form-group">
                        <label class="label" for="email">Correo Electrónico</label>
                        <input class="input" id="email" name="email" type="email" placeholder="tu@email.com" required>
                    </div>
                    <div class="form-group">
                        <label class="label" for="password">Contraseña</label>
                        <input class="input" id="password" name="password" type="password" placeholder="••••••••" required>
                    </div>
                    <button type="submit" class="btn btn-primary btn-full">Iniciar Sesión</button>
                </form>
            </div>
            
            <div class="card-footer text-center">
                <p class="card-desc">¿No tienes una cuenta?</p>
                <a href="${pageContext.request.contextPath}/registrarCuenta" class="btn btn-outline btn-full">Crear Cuenta</a>
            </div>
        </div>
    </div>
</body>
</html>