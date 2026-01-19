<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<div class="card" style="border-left: 4px solid #ef4444; background: #fff7f7; padding: 1rem; margin-bottom: 1rem;">
    <div class="card-content">
        <h4 style="color:#b91c1c; margin-bottom: 0.5rem;">Error</h4>
        <p style="color: #374151;">
            <c:choose>
                <c:when test="${sessionScope.flash_message == 'mostrarMontoInvalido'}">El monto ingresado no es válido. Debe ser mayor que 0.</c:when>
                <c:when test="${sessionScope.flash_message == 'mostrarFondosInsuficientes'}">No tienes fondos suficientes en la billetera para realizar esta apuesta.</c:when>
                <c:otherwise>${sessionScope.flash_message}</c:otherwise>
            </c:choose>
        </p>
    </div>
</div>