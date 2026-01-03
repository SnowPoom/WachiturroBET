<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- Fragment: muestra un error de operación (recarga/retiro). Espera atributos request: message, operacion --%>
<%
    String message = (String) request.getAttribute("message");
    String operacion = (String) request.getAttribute("operacion");
    String titulo = "Error en la operación";
    if ("RETIRO".equalsIgnoreCase(operacion)) {
        titulo = "Error al retirar";
    } else if ("RECARGA".equalsIgnoreCase(operacion)) {
        titulo = "Error al recargar";
    }
%>
<div class="result-err">
    <strong><%= titulo %>:</strong>
    <span><%= message != null ? message : "Ocurrió un error." %></span>
</div>