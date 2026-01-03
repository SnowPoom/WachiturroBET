<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- Fragment: muestra la confirmación de operación (recarga/retiro). Espera atributos de request: message, monto, usuarioName, operacion --%>
<%
    String message = (String) request.getAttribute("message");
    Object montoObj = request.getAttribute("monto");
    String usuarioName = (String) request.getAttribute("usuarioName");
    String operacion = (String) request.getAttribute("operacion");
    String titulo = "Operación exitosa";
    if ("RETIRO".equalsIgnoreCase(operacion)) {
        titulo = "Retiro exitoso";
        
    } else if ("RECARGA".equalsIgnoreCase(operacion)) {
        titulo = "Recarga exitosa";
    }
%>
<div class="result-ok">
    <strong><%= titulo %>:</strong>
    <span><%= message != null ? message : "Operación completada." %></span>
    <% if (montoObj != null) { %>
        <div><strong>Monto:</strong> $<%= montoObj %></div>
    <% } %>
    <% if (usuarioName != null) { %>
        <div><strong>Usuario:</strong> <%= usuarioName %></div>
    <% } %>
</div>