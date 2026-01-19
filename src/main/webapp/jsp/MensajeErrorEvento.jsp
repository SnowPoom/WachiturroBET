<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%-- 
    Fragment: muestra error al crear evento.
    Espera atributos de request: message
--%>
<%
    String message = (String) request.getAttribute("message");
%>
<div class="result-err">
    <strong>Error:</strong>
    <span><%= message != null ? message : "Ocurrió un error al procesar el evento." %></span>
</div>