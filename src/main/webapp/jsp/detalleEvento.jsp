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
            <form action="${pageContext.request.contextPath}/apuesta" method="post">
                <input type="hidden" name="action" value="ingresarMonto" />
                <input type="hidden" name="idEvento" value="${eventoDetalle.id}" />
                <input type="number" name="monto" class="input margin" placeholder="0.00" step="0.01" required style="height:2.2rem; width: 200px;">

                <div class="grid-2" style="margin-top: 1rem;">
                    <c:forEach var="p" items="${eventoDetalle.pronosticos}">
                        <div>
                            <label class="card-desc">${p.descripcion}</label>
                            <div class="flex-center" style="gap: 0.5rem; margin-top: 0.5rem;">
                                <input type="radio" name="idPronostico" value="${p.id}" required />
                                <button type="submit" class="btn btn-outline" style="flex-direction: column; padding: 0.75rem;">
                                    <span>Apostar</span>
                                    <span class="text-purple font-bold">${p.cuotaActual}</span>
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
                    Volver a la Lista
                </a>
            </div>
            </div>
    </div>
</div>
</body>
</html>