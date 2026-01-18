<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Nuevo Evento</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/styles.css">
</head>
<body>
    <header class="navbar">
        <div class="container nav-content">
            <a href="homeAdmin.jsp" class="logo">WachiturroBet Admin</a>
        </div>
    </header>

    <div class="container" style="padding-top: 2rem; max-width: 600px;">
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
                        <a href="homeAdmin.jsp" class="btn btn-ghost btn-full">Cancelar</a>
                        <button type="submit" class="btn btn-primary btn-full">Crear Evento</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>