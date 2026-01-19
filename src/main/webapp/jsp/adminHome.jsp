<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="modelo.entidades.Evento" %> 
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>WachiturroBet - Admin Home</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/styles.css">
    <style>
        .admin-table { width: 100%; border-collapse: collapse; color: var(--text-muted); }
        .admin-table th { text-align: left; padding: 1rem; color: #fff; border-bottom: 1px solid var(--color-border); }
        .admin-table td { padding: 1rem; border-bottom: 1px solid rgba(168, 85, 247, 0.1); }
        .admin-table tr:hover { background: rgba(168, 85, 247, 0.05); }
        .radio-custom { accent-color: var(--color-purple); transform: scale(1.2); cursor: pointer; }
    </style>
</head>
<body>
    <header class="navbar">
        <div class="container nav-content">
            <a href="${pageContext.request.contextPath}/gestionarEventos" class="logo">
                <div class="logo-icon"><img src="${pageContext.request.contextPath}/jsp/image.png" alt="icon"></div>
                <span>WachiturroBet Admin</span>
            </a>
            <div class="flex-center gap-2">
                <a href="${pageContext.request.contextPath}/initTestData?forceUser=true" class="btn btn-ghost" 
                   style="color: #4ade80; border: 1px dashed rgba(74, 222, 128, 0.5); margin-right: 10px;" 
                   title="Cambiar a sesión de Usuario">
                    <svg class="icon" viewBox="0 0 24 24"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
                    Modo Usuario
                </a>
                
                <a href="profile.jsp" class="btn btn-ghost">Salir</a>
            </div>
        </div>
    </header>

    <div class="container" style="padding-top: 2rem;">
        
        <div class="text-center mb-8">
            <h1 class="text-4xl text-purple">Panel de Administración</h1>
            <p class="card-desc">Gestión de Eventos y Pronósticos</p>
        </div>

        <div class="card">
            <div class="card-content flex-between" style="padding: 1.5rem;">
                <h3 class="card-title" style="margin:0;">Listado de Eventos</h3>
                
                <div class="flex-center gap-2">
                    <a href="${pageContext.request.contextPath}/crearEvento?ruta=crear&reset=true" class="btn btn-primary">
                        <svg class="icon" viewBox="0 0 24 24"><path d="M12 5v14M5 12h14"/></svg>
                        Crear Evento
                    </a>
                </div>
            </div>
        </div>

        <form id="formAdmin" method="get">
            <div class="card">
                <div class="card-content" style="padding: 0;">
                    <table class="admin-table">
                        <thead>
                            <tr>
                                <th style="width: 50px;">Sel.</th>
                                <th>Evento</th>
                                <th>Fecha</th>
                                <th>Categoría</th>
                                <th>Estado</th>
                            </tr>
                        </thead>
                        <tbody>
                            <% 
                                List<Evento> lista = (List<Evento>) request.getAttribute("listaEventos");
                                if (lista != null && !lista.isEmpty()) {
                                    for (Evento e : lista) {
                            %>
                            <tr>
                                <td><input type="radio" name="idEvento" value="<%= e.getId() %>" class="radio-custom" required></td>
                                <td style="color: white; font-weight: 500;"><%= e.getNombre() %></td>
                                <td><%= e.getFechaFormateada() %></td>
                                <td><%= e.getCategoria() != null ? e.getCategoria().name() : "-" %></td>
                                <td>
                                    <% 
                                        String estadoLabel = e.isEstado() ? "Abierto" : "Cerrado";
                                        String badgeClass = e.isEstado() ? "badge-green" : "badge-purple";
                                    %>
                                    <span class="badge <%= badgeClass %>"><%= estadoLabel %></span>
                                </td>
                            </tr>
                            <%      
                                    }
                                } else { %>
                            <tr>
                                <td colspan="5">No hay eventos registrados.</td>
                            </tr>
                            <% } %>
                        </tbody>
                    </table>
                </div>
                
                <div class="card-footer" style="flex-direction: row; justify-content: flex-end; gap: 1rem; border-top: 1px solid var(--color-border);">
                    
                    <button type="button" onclick="submitAction('PronosticosController')" class="btn btn-outline">
                        <svg class="icon" viewBox="0 0 24 24"><path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"/><path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"/></svg>
                        Gestionar Pronósticos
                    </button>

                    <button type="button" onclick="submitAction('FinalizarEventoController')" class="btn btn-ghost" style="color: #f87171; border: 1px solid rgba(248,113,113,0.3);">
                        <svg class="icon" viewBox="0 0 24 24"><path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"/><polyline points="22 4 12 14.01 9 11.01"/></svg>
                        Finalizar Evento
                    </button>
                </div>
            </div>
        </form>
    </div>

    <script>
        function submitAction(controller) {
            const form = document.getElementById('formAdmin');
            const selected = form.querySelector('input[name="idEvento"]:checked');
            
            if (!selected) {
                alert("Por favor, selecciona un evento de la lista primero.");
                return;
            }

            if (controller === 'FinalizarEventoController') {
                form.action = "${pageContext.request.contextPath}/finalizarEvento"; 
            } else {
                form.action = "${pageContext.request.contextPath}/gestionarPronosticos";
            }
            form.submit();
        }
    </script>
</body>
</html>