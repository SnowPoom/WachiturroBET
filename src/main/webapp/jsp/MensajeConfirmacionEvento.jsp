<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- 
    Fragment: muestra confirmación de creación de evento.
    Espera atributos de request: message
--%>
<%
    String message = (String) request.getAttribute("message");
%>
<div class="result-ok">
    <strong>Operación Exitosa:</strong>
    <span><%= message != null ? message : "Evento creado correctamente." %></span>
</div>