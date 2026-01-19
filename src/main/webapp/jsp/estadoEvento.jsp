<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="modelo.entidades.Pronostico" %>
<%@ page import="modelo.entidades.Evento" %>
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
        
        /* Estilos para mensajes flash */
        .result-ok { background: #dcfce7; color: #14532d; padding: 0.75rem; border-radius: 6px; margin-bottom: 1rem; }
        .result-err { background: #fee2e2; color: #7f1d1d; padding: 0.75rem; border-radius: 6px; margin-bottom: 1rem; }
    </style>
</head>
<body>
    <header class="navbar">
        <div class="container nav-content">
            <a href="${pageContext.request.contextPath}/gestionarEventos" class="logo">WachiturroBet Admin</a>
        </div>
    </header>

    <div class="container" style="padding-top: 2rem; max-width: 700px;">
        
        <%-- LÓGICA DE MENSAJES FLASH (Igual que en pronosticos.jsp) --%>
        <%
            String flashStatus = (String) session.getAttribute("flash_status");
            if (flashStatus != null) {
                request.setAttribute("status", flashStatus);
                request.setAttribute("message", session.getAttribute("flash_message"));
                session.removeAttribute("flash_status");
                session.removeAttribute("flash_message");
            }
            String status = (String) request.getAttribute("status");
        %>

        <%-- Fragments de mensajes --%>
        <% if (status != null && "OK".equals(status)) { %>
             <jsp:include page="/jsp/MensajeConfirmacionEvento.jsp" />
        <% } else if (status != null) { %>
             <jsp:include page="/jsp/MensajeErrorEvento.jsp" />
        <% } %>

        <div class="card" style="border-color: rgba(248,113,113,0.3);">
            <div class="card-header text-center">
                <h2 class="card-title text-purple">Finalizar Evento</h2>
                <p class="card-desc">Selecciona el pronóstico ganador para cerrar las apuestas.</p>
            </div>

            <div class="card-content">
                <div style="background: rgba(168, 85, 247, 0.1); padding: 1rem; border-radius: 0.5rem; margin-bottom: 2rem;">
                    <h3 class="text-2xl mb-2"><%= request.getAttribute("nombreEvento") != null ? request.getAttribute("nombreEvento") : "Evento" %></h3>
                    <p class="card-desc"><%= request.getAttribute("descEvento") != null ? request.getAttribute("descEvento") : "Sin descripción" %></p>
                </div>

                <form action="${pageContext.request.contextPath}/finalizarEvento" method="post" onsubmit="return confirm('ATENCIÓN: Esta acción es irreversible.\n\n¿Confirmas que este es el resultado final?');">
                    
                    <%-- Ruteador: ruta="confirmar" --%>
                    <input type="hidden" name="ruta" value="confirmar" />
                    
                    <%-- Recuperamos el ID del evento del objeto pasado por el controlador --%>
                    <% Evento evt = (Evento) request.getAttribute("evento"); %>
                    <input type="hidden" name="idEvento" value="<%= (evt != null) ? evt.getId() : request.getParameter("idEvento") %>" />
                    
                    <label class="label mb-4">Selecciona el Resultado Ganador:</label>
                    
                    <div style="display: flex; flex-direction: column; gap: 0.75rem;">
                        <% 
                            List<Pronostico> lista = (List<Pronostico>) request.getAttribute("listaPronosticos");
                            if (lista != null && !lista.isEmpty()) {
                                for (Pronostico p : lista) {
                        %>
                            <label class="forecast-item">
                                <span style="color: white;"><%= p.getDescripcion() %></span>
                                <div class="flex-center gap-2">
                                    <span class="badge badge-purple">x<%= p.getCuotaActual() %></span>
                                    <input type="radio" name="idPronosticoGanador" value="<%= p.getId() %>" class="radio-win" required>
                                </div>
                            </label>
                        <% 
                                }
                            } else {
                        %>
                            <div class="text-center" style="color: var(--text-muted); padding: 1rem;">
                                No hay pronósticos registrados para este evento. No se puede finalizar.
                            </div>
                        <% } %>
                    </div>

                    <%-- Solo mostramos el botón si hay pronósticos --%>
                    <% if (lista != null && !lista.isEmpty()) { %>
                        <div class="flex-center gap-4" style="margin-top: 2rem;">
                            <a href="${pageContext.request.contextPath}/gestionarEventos" class="btn btn-ghost btn-full">Cancelar</a>
                            <button type="submit" class="btn btn-primary btn-full" style="background: linear-gradient(135deg, #f87171, #ef4444);">
                                Finalizar Definitivamente
                            </button>
                        </div>
                    <% } else { %>
                        <div class="flex-center gap-4" style="margin-top: 2rem;">
                             <a href="${pageContext.request.contextPath}/gestionarEventos" class="btn btn-outline btn-full">Volver</a>
                        </div>
                    <% } %>
                </form>
            </div>
        </div>
    </div>
</body>
</html>