<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>Resultados de Búsqueda</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/styles.css">
</head>
<body>
    <header class="navbar">
        <div class="container nav-content">
            <a href="${pageContext.request.contextPath}/ListarEventosController?ruta=entrar" class="logo">
                <div class="logo-icon"><img src="${pageContext.request.contextPath}/jsp/image.png" alt="icon"></div>
                <span>WachiturroBet</span>
            </a>
            <div class="flex-center gap-2">
                <a href="${pageContext.request.contextPath}/ListarEventosController?ruta=entrar" class="btn btn-ghost">
                    <svg class="icon" viewBox="0 0 24 24" style="width:1.2em; height:1.2em; margin-right:0.5rem; vertical-align: bottom;">
                        <path d="M19 12H5M12 19l-7-7 7-7" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    Volver
                </a>
            </div>
        </div>
    </header>

    <div class="container" style="padding-top: 2rem;">
        
        <div class="text-center mb-8">
            <h2 class="text-4xl text-purple">Resultados de Búsqueda</h2>
        </div>

        <%-- CASO 1: NO HAY RESULTADOS (Mensaje Error) --%>
        <c:if test="${not empty mensajeError}">
            <div class="card" style="border-color: rgba(236, 72, 153, 0.5); background: rgba(236, 72, 153, 0.05);">
                <div class="card-content text-center" style="padding: 3rem;">
                    <svg class="icon" style="width: 4rem; height: 4rem; color: #ec4899; margin-bottom: 1rem;" viewBox="0 0 24 24"><circle cx="11" cy="11" r="8"></circle><line x1="21" x2="16.65" y1="21" y2="16.65"></line><line x1="8" y1="11" x2="14" y2="11"></line></svg>
                    <h3 class="text-2xl">${mensajeError}</h3>
                    <a href="${pageContext.request.contextPath}/ListarEventosController?ruta=entrar" class="btn btn-primary mt-4" style="margin-top: 1.5rem;">Intentar otra búsqueda</a>
                </div>
            </div>
        </c:if>

        <%-- CASO 2: HAY RESULTADOS --%>
        <c:if test="${not empty resultadosBusqueda}">
            <div class="grid-2">
                <c:forEach var="evento" items="${resultadosBusqueda}">
                    <div class="card">
                        <div class="card-header">
                            <div class="flex-between mb-4">
                                <span class="badge badge-purple">${evento.categoria}</span>
                                <div class="flex-center gap-2 card-desc">
                                    <svg class="icon" style="width: 1rem; height: 1rem;" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                                    <span>${evento.fechaFormateada}</span>
                                </div>
                            </div>
                            <h3 class="card-title">${evento.nombre}</h3>
                            <p class="card-desc">${evento.descripcion}</p>
                        </div>
                        
                        <div class="card-content">
                        	<div class="grid-2" style="gap: 0.5rem;">
                            	<a href="${pageContext.request.contextPath}/ApuestaController?ruta=seleccionarEvento&id=${evento.id}" 
                               	   class="btn btn-outline"
                               	   style="text-decoration: none; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 1rem; width: 100%;">
                                
                                	<span>Ver Evento</span>
                            	</a>
                        	</div>    	
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:if>

    </div>
</body>
</html>