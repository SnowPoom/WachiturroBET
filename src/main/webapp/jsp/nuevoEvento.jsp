<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Nuevo Evento</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/styles.css">
    <style>
        /* Estilos necesarios para los mensajes (tomados de billetera.jsp) */
        .result-ok { background: #dcfce7; color: #14532d; padding: 0.75rem; border-radius: 6px; margin-bottom: 1rem; }
        .result-err { background: #fee2e2; color: #7f1d1d; padding: 0.75rem; border-radius: 6px; margin-bottom: 1rem; }
    </style>
</head>
<body>
    <header class="navbar">
        <div class="container nav-content">
            <%-- Corregido el link al controlador --%>
            <a href="${pageContext.request.contextPath}/gestionarEventos" class="logo">WachiturroBet Admin</a>
        </div>
    </header>

    <div class="container" style="padding-top: 2rem; max-width: 600px;">
        
        <%-- LÓGICA DE MENSAJES FLASH (Igual que en Billetera) --%>
        <%
            // 1. Verificar si hay mensajes en sesión
            String flashStatus = (String) session.getAttribute("flash_status");
            
            if (flashStatus != null) {
                // 2. Pasarlos al request para los fragmentos
                request.setAttribute("status", flashStatus);
                request.setAttribute("message", session.getAttribute("flash_message"));
                
                // 3. Limpiar sesión (Flash scope)
                session.removeAttribute("flash_status");
                session.removeAttribute("flash_message");
            }
            
            // Variable local
            String status = (String) request.getAttribute("status");
        %>

        <%-- Inclusión dinámica de los fragmentos según el estado --%>
        <% if (status != null && "OK".equals(status)) { %>
            <jsp:include page="/jsp/MensajeConfirmacionEvento.jsp" />
        <% } else if (status != null) { %>
            <jsp:include page="/jsp/MensajeErrorEvento.jsp" />
        <% } %>

        <div class="card">
            <div class="card-header">
                <h2 class="card-title">Nuevo Evento</h2>
                <p class="card-desc">Ingresa los detalles para publicar un nuevo evento.</p>
            </div>
            
            <div class="card-content">
                <form action="${pageContext.request.contextPath}/crearEvento" method="post" onsubmit="return confirm('¿Estás seguro de crear este evento?');">
                    <input type="hidden" name="ruta" value="guardar" />
                    
                    <div class="form-group">
                        <label class="label">Nombre del Evento</label>
                        <input type="text" name="nombre" class="input" placeholder="Ej: Final Champions League" required>
                    </div>

                    <div class="grid-2">
                        <div class="form-group">
                            <label class="label">Fecha y Hora</label>
                            <input type="datetime-local" name="fecha" class="input" required>
                        </div>
                        <div class="form-group">
                            <label class="label">Categoría</label>
                            <select name="categoria" class="input" style="appearance: none;">
                                <option value="DEPORTES">Deportes</option>
                                <option value="ESPORTS">E-Sports</option>
                                <option value="POLITICA">Política</option>
                                <option value="OTROS">Otros</option>
                            </select>
                        </div>
                    </div>

                    <div class="form-group">
                        <label class="label">Descripción</label>
                        <textarea name="descripcion" class="input" style="height: 100px; padding-top: 0.5rem;" placeholder="Detalles del evento..."></textarea>
                    </div>

                    <div class="flex-center gap-4" style="margin-top: 2rem;">
                        <%-- Link corregido para no romper la navegación --%>
                        <a href="${pageContext.request.contextPath}/gestionarEventos" class="btn btn-ghost btn-full">Cancelar</a>
                        <button type="submit" class="btn btn-primary btn-full">Crear Evento</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>