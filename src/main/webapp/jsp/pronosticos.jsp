<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestionar Pronósticos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/styles.css">
</head>
<body>
    <header class="navbar">
        <div class="container nav-content">
            <a href="homeAdmin.jsp" class="logo">WachiturroBet Admin</a>
        </div>
    </header>

    <div class="container" style="padding-top: 2rem;">
        
        <div class="text-center mb-8">
            <h2 class="text-4xl text-purple">Gestionar Pronósticos</h2>
            <p class="card-desc">Evento: <%= request.getAttribute("nombreEvento") != null ? request.getAttribute("nombreEvento") : "Evento Seleccionado" %></p>
        </div>

        <div class="grid-2">
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title">Pronósticos Activos</h3>
                </div>
                <div class="card-content">
                    <div style="display: flex; flex-direction: column; gap: 1rem;">
                        <%-- Aquí iterarías la lista de pronósticos del request --%>
                        
                        <div class="flex-between" style="background: rgba(15, 23, 42, 0.5); padding: 1rem; border-radius: 0.5rem;">
                            <div>
                                <div style="font-weight: 500; color: white;">Local Gana</div>
                                <div class="card-desc">Id: 101</div>
                            </div>
                            <span class="badge badge-purple" style="font-size: 1rem;">Cuota: 1.50</span>
                        </div>

                        <div class="flex-between" style="background: rgba(15, 23, 42, 0.5); padding: 1rem; border-radius: 0.5rem;">
                            <div>
                                <div style="font-weight: 500; color: white;">Empate</div>
                                <div class="card-desc">Id: 102</div>
                            </div>
                            <span class="badge badge-purple" style="font-size: 1rem;">Cuota: 3.20</span>
                        </div>

                    </div>
                    
                    <div style="margin-top: 1.5rem; text-align: center;">
                        <a href="homeAdmin.jsp" class="btn btn-outline">Volver al Home</a>
                    </div>
                </div>
            </div>

            <div class="card" style="border-color: var(--color-purple);">
                <div class="card-header">
                    <h3 class="card-title">Nuevo Pronóstico</h3>
                    <p class="card-desc">Añadir una opción para este evento</p>
                </div>
                <div class="card-content">
                    <form action="${pageContext.request.contextPath}/crearPronostico" method="post" onsubmit="return confirm('¿Agregar este pronóstico al evento?');">
                        <input type="hidden" name="idEvento" value="${param.idEvento}" />
                        
                        <div class="form-group">
                            <label class="label">Descripción</label>
                            <input type="text" name="descripcion" class="input" placeholder="Ej: Visitante Gana" required>
                        </div>
                        
                        <div class="form-group">
                            <label class="label">Cuota (Multiplicador)</label>
                            <input type="number" step="0.01" name="cuota" class="input" placeholder="Ej: 2.10" required>
                        </div>

                        <div class="flex-center gap-4" style="margin-top: 1.5rem;">
                            <button type="reset" class="btn btn-ghost">Limpiar</button>
                            <button type="submit" class="btn btn-primary btn-full">
                                <svg class="icon" viewBox="0 0 24 24"><path d="M12 5v14M5 12h14"/></svg>
                                Agregar Pronóstico
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>
</html>