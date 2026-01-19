<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>WachiturroBet - Historial</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/styles.css">
    <style>
        /* Use the same table styling as adminHome.jsp */
        .admin-table { width: 100%; border-collapse: collapse; color: var(--text-muted); }
        .admin-table th { text-align: left; padding: 1rem; color: #fff; border-bottom: 1px solid var(--color-border); }
        .admin-table td { padding: 1rem; border-bottom: 1px solid rgba(168, 85, 247, 0.1); }
        .admin-table tr:hover { background: rgba(168, 85, 247, 0.05); }

        /* Keep layout helpers used in this page */
        .table-container { width: 100%; overflow-x: auto; }

        /* Very thin compact single-line filter bar */
        .filter-bar { display: flex; gap: 0.35rem; align-items: center; padding: 0.12rem; border-radius: 0.375rem; }
        .filter-bar .form-group { margin: 0; display: flex; align-items: center; }
        .small-label { color: var(--text-muted); font-size: 0.78rem; margin-right: 0.35rem; white-space: nowrap; }
        .date-field { display: flex; align-items: center; gap: 0.35rem; color: white;  }
        .calendar-icon { width: 1rem; height: 1rem; flex-shrink: 0; color: white; }
        .filter-bar .input, .filter-bar select { height: 1.6rem; padding: 0 0.4rem; font-size: 0.8rem; }
        .filter-bar .btn { height: 1.6rem; padding: 0 0.6rem; font-size: 0.8rem; }
        /* Remove underline below the filter buttons */
        .filter-bar-wrapper { border-bottom: none; padding: 0.08rem 0.75rem; }
        .filtro-error { color: #fca5a5; font-size: 0.85rem; margin-top: 0.25rem; }

        /* Ensure anchor buttons don't show underline */
        .filter-bar a { text-decoration: none; }
    </style>
</head>
<body>

    <header class="navbar">
        <div class="container nav-content">
            <a href="ListarEventosController?ruta=entrar" class="logo">
                <div class="logo-icon"><img src="${pageContext.request.contextPath}/jsp/image.png" alt="icon"></div>
                <span>WachiturroBet</span>
            </a>
            
            <div class="flex-center gap-2">
                <a href="billetera.jsp" class="btn btn-ghost" title="Cartera">
                    <svg class="icon" viewBox="0 0 24 24"><path d="M21 12V7H5a2 2 0 0 1 0-4h14v4"/><path d="M3 5v14a2 2 0 0 0 2 2h16v-5"/><path d="M18 12a2 2 0 0 0 0 4h4v-4Z"/></svg>
                </a>
                <a href="profile.jsp" class="btn btn-ghost" title="Perfil">
                    <svg class="icon" viewBox="0 0 24 24"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                </a>
            </div>
        </div>
    </header>

    <div class="container" style="padding-top: 2rem; padding-bottom: 2rem;">

        <div class="text-center mb-8">
            <h1 class="text-4xl flex-center gap-2 mb-2">
                <svg class="icon" style="width: 2.5rem; height: 2.5rem; color: var(--color-purple);" viewBox="0 0 24 24"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path><polyline points="14 2 14 8 20 8"></polyline><line x1="16" y1="13" x2="8" y2="13"></line><line x1="16" y1="17" x2="8" y2="17"></line><polyline points="10 9 9 9 8 9"></polyline></svg>
                Historial de Apuestas
            </h1>
            <p class="card-desc">Consulta el estado y resultados de tus predicciones</p>
        </div>

        <div class="card mb-8">
            <!-- removed the header title to keep only a slim filter line -->
            <div class="card-content" style="padding-top: 0.5rem;">
                <form action="historial" method="post">
                    <input type="hidden" name="action" value="filtrar">

                    <div class="filter-bar-wrapper">
                        <div class="filter-bar">
                            <div class="form-group date-field">
                                <span class="small-label">Fecha Inicio:</span>
                                <input type="date" name="fechaInicio" class="input" value="${fechaInicio}">
                            </div>

                            <div class="form-group date-field">
                                <span class="small-label">Fecha Fin:</span>
                                <input type="date" name="fechaFin" class="input" value="${fechaFin}">
                            </div>

                            <div class="form-group" style="margin-left: 0.4rem;">
                                <label class="small-label" style="margin-right:0.5rem;">Estado:</label>
                                <select name="estado" class="input">
                                    <option value="" ${empty estadoFiltro ? 'selected' : ''}>Todos</option>
                                    <option value="GANADA" ${estadoFiltro == 'GANADA' ? 'selected' : ''}>Ganada</option>
                                    <option value="PERDIDA" ${estadoFiltro == 'PERDIDA' ? 'selected' : ''}>Perdida</option>
                                    <option value="PENDIENTE" ${estadoFiltro == 'PENDIENTE' ? 'selected' : ''}>Pendiente</option>
                                </select>
                            </div>

                            <div class="form-group" style="margin-left: auto; display: flex; gap: 0.35rem;">
                                <button type="submit" class="btn btn-primary">Filtrar</button>
                                <a href="historial?action=limpiarFiltros" class="btn btn-outline">Limpiar</a>
                            </div>
                        </div>
                    </div>

                    <c:if test="${not empty filtroError}">
                        <div class="filtro-error">${filtroError}</div>
                    </c:if>
                </form>
            </div>
        </div>

        <div class="card">
            <div class="card-header flex-between">
                <div>
                    <h3 class="card-title">Listado de Apuestas</h3>
                    <p class="card-desc">Mostrando tus últimos movimientos</p>
                </div>
                <span class="badge badge-purple">
                    Total: ${not empty apuestas ? apuestas.size() : 0}
                </span>
            </div>

            <div class="card-content">
                
                <c:if test="${sinRegistros}">
                    <div class="text-center" style="padding: 3rem; color: var(--text-muted);">
                        <svg class="icon" style="width: 3rem; height: 3rem; margin-bottom: 1rem; opacity: 0.5;" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/><line x1="12" y1="8" x2="12" y2="12"/><line x1="12" y1="16" x2="12.01" y2="16"/></svg>
                        <p>No tienes apuestas registradas en tu historial.</p>
                        <a href="ListarEventosController" class="btn btn-primary margin">¡Haz tu primera apuesta!</a>
                    </div>
                </c:if>

                <c:if test="${sinCoincidencias}">
                    <div class="text-center" style="padding: 2rem; color: var(--text-muted);">
                        <p>No se encontraron apuestas con esos criterios de búsqueda.</p>
                        <a href="historial?action=limpiarFiltros" class="btn btn-ghost margin">Limpiar filtros</a>
                    </div>
                </c:if>

                <c:if test="${not empty apuestas}">
                    <div class="table-container">
                        <table class="admin-table">
                            <thead>
                                <tr>
                                    <th>Evento</th>
                                    <th>Fecha</th>
                                    <th>Monto</th>
                                    <th>Estado</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="apuesta" items="${apuestas}">
                                    <tr>
                                        <td style="color: white; font-weight: 500;">
                                            <div>
                                                <c:out value="${apuesta.pronostico.evento.nombre}" default="Evento Desconocido"/>
                                            </div>
                                            <div class="card-desc" style="font-size: 0.75rem;">
                                                <c:out value="${apuesta.pronostico.evento.categoria}" default="General"/>
                                            </div>
                                        </td>

                                        <td style="color: var(--text-muted);">
                                            <c:choose>
                                                <c:when test="${not empty fechasMap[apuesta.id]}">
                                                    ${fechasMap[apuesta.id]}
                                                </c:when>
                                                <c:otherwise>
                                                    <c:out value="${apuesta.fecha}"/>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>

                                        <td style="font-weight: 500;">
                                            $ <fmt:formatNumber value="${apuesta.monto}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                                        </td>

                                        <td>
                                            <c:choose>
                                                <c:when test="${apuesta.estado == 'GANADA'}">
                                                    <span class="badge badge-green">Ganada</span>
                                                </c:when>
                                                <c:when test="${apuesta.estado == 'PERDIDA'}">
                                                    <span class="badge badge-purple">Perdida</span>
                                                </c:when>
                                                <c:otherwise>
                                                    <span class="badge badge-purple">Pendiente</span>
                                                </c:otherwise>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>
            </div>
            
            <c:if test="${not empty apuestas}">
                <div class="card-footer border-top" style="border-top: 1px solid var(--color-border); flex-direction: row; justify-content: space-between; align-items: center;">
                    <span class="card-desc">Mostrando ${apuestas.size()} resultados</span>
                    <div class="flex-center gap-2">
                        <button class="btn btn-ghost" style="padding: 0.25rem 0.5rem;" disabled>&lt;</button>
                        <button class="btn btn-ghost" style="padding: 0.25rem 0.5rem; background: rgba(168,85,247,0.2);">1</button>
                        <button class="btn btn-ghost" style="padding: 0.25rem 0.5rem;">2</button>
                        <button class="btn btn-ghost" style="padding: 0.25rem 0.5rem;" disabled>&gt;</button>
                    </div>
                </div>
            </c:if>
        </div>

    </div>

</body>
</html>