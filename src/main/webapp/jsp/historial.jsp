<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8" />
    <title>Historial de Apuestas</title>
    <link rel="stylesheet" href="/jsp/styles.css" />
</head>
<body>
<h1>Historial de Apuestas</h1>

<form method="post" action="/historial?action=filtrar">
    <label>Fecha inicio:
        <input type="datetime-local" name="fechaInicio" />
    </label>
    <label>Fecha fin:
        <input type="datetime-local" name="fechaFin" />
    </label>
    <label>Estado:
        <select name="estado">
            <option value="">--Cualquiera--</option>
            <option value="PENDIENTE">PENDIENTE</option>
            <option value="GANADA">GANADA</option>
            <option value="PERDIDA">PERDIDA</option>
        </select>
    </label>
    <button type="submit">Filtrar</button>
    <a href="/historial?action=limpiarFiltros">Limpiar filtros</a>
</form>

<c:if test="${not empty sinRegistros}">
    <jsp:include page="/jsp/MensajeSinRegistros.jsp" />
</c:if>

<c:if test="${not empty sinCoincidencias}">
    <jsp:include page="/jsp/MensajeSinCoincidencias.jsp" />
</c:if>

<table border="1" cellpadding="6" cellspacing="0">
    <thead>
    <tr>
        <th>Fecha</th>
        <th>Evento</th>
        <th>Selección</th>
        <th>Monto</th>
        <th>Cuota Registrada</th>
        <th>Estado</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach var="a" items="${apuestas}">
        <tr>
            <td><c:out value="${a.fecha}" /></td>
            <td><c:out value="${a.pronostico != null ? a.pronostico.evento.nombre : 'N/A'}" /></td>
            <td><c:out value="${a.pronostico != null ? a.pronostico.descripcion : 'N/A'}" /></td>
            <td><c:out value="${a.monto}" /></td>
            <td><c:out value="${a.cuotaRegistrada}" /></td>
            <td><c:out value="${a.estado}" /></td>
        </tr>
    </c:forEach>
    </tbody>
</table>

</body>
</html>
