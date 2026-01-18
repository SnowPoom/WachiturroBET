<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Finalizar Evento</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/styles.css">
    <style>
        .forecast-item {
            display: flex; align-items: center; justify-content: space-between;
            background: rgba(15, 23, 42, 0.5); padding: 1rem;
            border-radius: 0.5rem; border: 1px solid transparent; cursor: pointer;
            transition: all 0.2s;
        }
        .forecast-item:hover { border-color: var(--color-purple); }
        .radio-win { width: 1.25rem; height: 1.25rem; accent-color: #22c55e; }
    </style>
</head>
<body>
    <header class="navbar">
        <div class="container nav-content">
            <a href="homeAdmin.jsp" class="logo">WachiturroBet Admin</a>
        </div>
    </header>

    <div class="container" style="padding-top: 2rem; max-width: 700px;">
        
        <div class="card" style="border-color: rgba(248,113,113,0.3);">
            <div class="card-header text-center">
                <h2 class="card-title text-purple">Finalizar Evento</h2>
                <p class="card-desc">Selecciona el pronóstico ganador para cerrar las apuestas.</p>
            </div>

            <div class="card-content">
                <div style="background: rgba(168, 85, 247, 0.1); padding: 1rem; border-radius: 0.5rem; margin-bottom: 2rem;">
                    <h3 class="text-2xl mb-2"><%= request.getAttribute("nombreEvento") != null ? request.getAttribute("nombreEvento") : "Nombre Evento Demo" %></h3>
                    <p class="card-desc"><%= request.getAttribute("descEvento") != null ? request.getAttribute("descEvento") : "Descripción..." %></p>
                </div>

                <form action="${pageContext.request.contextPath}/procesarFinalizarEvento" method="post" onsubmit="return confirm('ATENCIÓN: Esta acción es irreversible.\n\n¿Confirmas que este es el resultado final?');">
                    <input type="hidden" name="idEvento" value="${param.idEvento}" />
                    
                    <label class="label mb-4">Selecciona el Resultado Ganador:</label>
                    
                    <div style="display: flex; flex-direction: column; gap: 0.75rem;">
                        <%-- Ejemplo estático --%>
                        <label class="forecast-item">
                            <span style="color: white;">Real Madrid Gana</span>
                            <div class="flex-center gap-2">
                                <span class="badge badge-purple">x1.50</span>
                                <input type="radio" name="idPronosticoGanador" value="101" class="radio-win" required>
                            </div>
                        </label>
                        
                        <label class="forecast-item">
                            <span style="color: white;">Empate</span>
                            <div class="flex-center gap-2">
                                <span class="badge badge-purple">x3.20</span>
                                <input type="radio" name="idPronosticoGanador" value="102" class="radio-win">
                            </div>
                        </label>
                        
                         <label class="forecast-item">
                            <span style="color: white;">Barcelona Gana</span>
                            <div class="flex-center gap-2">
                                <span class="badge badge-purple">x2.10</span>
                                <input type="radio" name="idPronosticoGanador" value="103" class="radio-win">
                            </div>
                        </label>
                    </div>

                    <div class="flex-center gap-4" style="margin-top: 2rem;">
                        <a href="homeAdmin.jsp" class="btn btn-ghost btn-full">Cancelar</a>
                        <button type="submit" class="btn btn-primary btn-full" style="background: linear-gradient(135deg, #f87171, #ef4444);">
                            Finalizar Definitivamente
                        </button>
                    </div>
                </form>
            </div>
        </div>
    </div>
</body>
</html>