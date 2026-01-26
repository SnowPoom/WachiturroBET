<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Detalle de Evento</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/styles.css">
</head>
<body>
<header class="navbar">
    <div class="container nav-content">
        <a href="index.jsp" class="logo">
            <div class="logo-icon"> <img src="${pageContext.request.contextPath}/jsp/image.png" alt="icon"></div>
            <span>WachiturroBet</span>
        </a>
    </div>
</header>

<div class="container" style="padding-top: 2rem; padding-bottom: 2rem;">

    <c:if test="${not empty sessionScope.flash_message}">
        <c:choose>
            <c:when test="${sessionScope.flash_message == 'mostrarMontoInvalido' || sessionScope.flash_status == 'ERROR'}">
                <jsp:include page="mensajeErrorApuesta.jsp" />
            </c:when>
            <c:otherwise>
                <jsp:include page="mensajeConfirmacionApuesta.jsp" />
            </c:otherwise>
        </c:choose>
        <c:remove var="flash_message" scope="session" />
        <c:remove var="flash_status" scope="session" />
        <c:remove var="flash_operacion" scope="session" />
    </c:if>

    <div class="card">
        <div class="card-header">
            <div class="flex-between mb-4">
                <span class="badge badge-purple">${eventoDetalle.categoria}</span>
                <div class="flex-center gap-2 card-desc">
                    <span>${eventoDetalle.fechaFormateada}</span>
                </div>
            </div>
            <h3 class="card-title">${eventoDetalle.nombre}</h3>
            <p class="card-desc">${eventoDetalle.descripcion}</p>
        </div>

        <div class="card-content">
            <label class="card-desc">Monto:</label>
            <form action="${pageContext.request.contextPath}/ApuestaController" method="post">
                <input type="hidden" name="ruta" value="ingresarMonto" />
                <input type="hidden" name="idEvento" value="${eventoDetalle.id}" />
                <input type="number" name="monto" class="input margin" placeholder="0.00" step="0.01" required style="height:2.2rem; width: 200px;">

                <div class="grid-2" style="margin-top: 1rem; column-gap: 2rem; row-gap: 0.8rem;">
                    <c:forEach var="p" items="${eventoDetalle.pronosticos}">
                        
                        <div style="display: flex; align-items: center; padding: 0.35rem 0;">
                            
                            <div style="flex: 0 0 40%; display: flex; align-items: center; gap: 0.5rem; padding-right: 0.5rem;">
                                <input type="radio" id="idPronostico-${p.id}" name="idPronostico" value="${p.id}" required style="cursor: pointer; flex-shrink: 0;" />
                                <label for="idPronostico-${p.id}" style="margin: 0; cursor: pointer; font-weight: 500; font-size: 0.95rem; white-space: nowrap; overflow: hidden; text-overflow: ellipsis;">
                                    ${p.descripcion}
                                </label>
                            </div>
                    
                            <div style="flex: 1;">
                                <button type="submit" class="btn btn-outline" style="width: 140px; padding: 0.3rem 0; display: flex; justify-content: center; align-items: center; white-space: nowrap; font-size: 0.9rem;">
                                    <span>Apostar</span>
                                    <span class="text-purple font-bold" style="margin-left: 0.4rem;">${p.cuotaActual}</span>
                                </button>
                            </div>
                            
                        </div>
                    </c:forEach>
                </div>
            </form>

            <div style="margin-top: 2rem; padding-top: 1rem; border-top: 1px solid rgba(168, 85, 247, 0.1); text-align: center;">
                <a href="${pageContext.request.contextPath}/ListarEventosController?ruta=entrar" class="btn btn-ghost">
                    <svg class="icon" viewBox="0 0 24 24" style="width:1.2em; height:1.2em; margin-right:0.5rem; vertical-align: bottom;">
                        <path d="M19 12H5M12 19l-7-7 7-7" stroke="currentColor" stroke-width="2" fill="none" stroke-linecap="round" stroke-linejoin="round"/>
                    </svg>
                    Volver
                </a>
            </div>
            </div>
    </div>
</div>
</body>
</html>