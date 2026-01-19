<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="modelo.entidades.Evento" %>
<%@ page import="modelo.entidades.Pronostico" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Gestionar Pronósticos</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/styles.css">
    <style>
        .result-ok { background: #dcfce7; color: #14532d; padding: 0.75rem; border-radius: 6px; margin-bottom: 1rem; }
        .result-err { background: #fee2e2; color: #7f1d1d; padding: 0.75rem; border-radius: 6px; margin-bottom: 1rem; }
        .card-disabled { opacity: 0.7; pointer-events: none; filter: grayscale(1); }
    </style>
</head>
<body>
    <header class="navbar">
        <div class="container nav-content">
            <a href="${pageContext.request.contextPath}/gestionarEventos" class="logo">WachiturroBet Admin</a>
        </div>
    </header>

    <div class="container" style="padding-top: 2rem;">
        
        <%-- LÓGICA DE MENSAJES FLASH --%>
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
        
        <%-- Fragments --%>
        <% if (status != null && "OK".equals(status)) { %>
             <jsp:include page="/jsp/MensajeConfirmacionEvento.jsp" />
        <% } else if (status != null) { %>
             <jsp:include page="/jsp/MensajeErrorEvento.jsp" />
        <% } %>

        <% 
            // Recuperamos el objeto evento para verificar el estado
            Evento evento = (Evento) request.getAttribute("evento");
            boolean estaAbierto = (evento != null && evento.isEstado());
        %>

        <div class="text-center mb-8">
            <h2 class="text-4xl text-purple">Gestionar Pronósticos</h2>
            <div class="flex-center gap-2">
                <p class="card-desc">Evento: <%= evento != null ? evento.getNombre() : "Desconocido" %></p>
                <% if (!estaAbierto) { %>
                    <span class="badge badge-purple">Cerrado</span>
                <% } else { %>
                    <span class="badge badge-green">Abierto</span>
                <% } %>
            </div>
        </div>

        <div class="grid-2">
            <div class="card">
                <div class="card-header">
                    <h3 class="card-title">Pronósticos Activos</h3>
                </div>
                <div class="card-content">
                    <div style="display: flex; flex-direction: column; gap: 1rem;">
                        <% 
                            List<Pronostico> lista = (List<Pronostico>) request.getAttribute("listaPronosticos");
                            if (lista != null && !lista.isEmpty()) {
                                for (Pronostico p : lista) {
                        %>
                            <div class="flex-between" style="background: rgba(15, 23, 42, 0.5); padding: 1rem; border-radius: 0.5rem;">
                                <div>
                                    <div style="font-weight: 500; color: white;"><%= p.getDescripcion() %></div>
                                    <div class="card-desc">Id: <%= p.getId() %></div>
                                </div>
                                <span class="badge badge-purple" style="font-size: 1rem;">Cuota: <%= p.getCuotaActual() %></span>
                            </div>
                        <% 
                                } 
                            } else { 
                        %>
                            <div class="text-center" style="color: var(--text-muted); padding: 1rem;">
                                No hay pronósticos registrados.
                            </div>
                        <% } %>
                    </div>
                    
                    <div style="margin-top: 1.5rem; text-align: center;">
                        <a href="${pageContext.request.contextPath}/gestionarEventos" class="btn btn-outline">Volver al Home</a>
                    </div>
                </div>
            </div>

            <% if (estaAbierto) { %>
                <div class="card" style="border-color: var(--color-purple);">
                    <div class="card-header">
                        <h3 class="card-title">Nuevo Pronóstico</h3>
                        <p class="card-desc">Añadir una opción para este evento</p>
                    </div>
                    <div class="card-content">
                        <form action="${pageContext.request.contextPath}/gestionarPronosticos" method="post" onsubmit="return confirm('¿Agregar este pronóstico?');">
                            <input type="hidden" name="ruta" value="guardar" />
                            <input type="hidden" name="idEvento" value="<%= evento.getId() %>" />
                            
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
            <% } else { %>
                <div class="card" style="border-color: rgba(248,113,113,0.3); background: rgba(239,68,68,0.05);">
                    <div class="card-content text-center" style="padding: 2rem;">
                        <svg class="icon" style="width: 3rem; height: 3rem; color: #f87171; margin-bottom: 1rem;" viewBox="0 0 24 24"><path d="M10.29 3.86L1.82 18a2 2 0 0 0 1.71 3h16.94a2 2 0 0 0 1.71-3L13.71 3.86a2 2 0 0 0-3.42 0z"/><line x1="12" y1="9" x2="12" y2="13"/><line x1="12" y1="17" x2="12.01" y2="17"/></svg>
                        <h3 class="text-2xl" style="color: #f87171;">Evento Finalizado</h3>
                        <p class="card-desc" style="margin-top: 0.5rem;">
                            Este evento se encuentra cerrado. No es posible agregar nuevos pronósticos ni modificar las cuotas.
                        </p>
                    </div>
                </div>
            <% } %>

        </div>
    </div>
</body>
</html>