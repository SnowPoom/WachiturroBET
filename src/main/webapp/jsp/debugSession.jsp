<%@ page import="modelo.entidades.UsuarioRegistrado" %>
<html>
<head><title>Debug Session</title></head>
<body>
<h2>Debug de sesión</h2>
<p>
<a href="<%=request.getContextPath()%>/initTestData">Crear datos de prueba (/initTestData)</a><br/>
<a href="<%=request.getContextPath()%>/recargarBilletera">Ir a recargarBilletera (GET)</a><br/>
<button onclick="window.location.href='<%=request.getContextPath()%>/recargarBilletera'">Ir a billetera</button>
</p>
<hr/>
<%
    Object cu = session.getAttribute("currentUser");
    Object cuId = session.getAttribute("currentUserId");
    Object cuSaldo = session.getAttribute("currentUserSaldo");
%>
<p>currentUser: <%= cu != null ? cu.toString() : "<null>" %></p>
<p>currentUserId: <%= cuId != null ? cuId.toString() : "<null>" %></p>
<p>currentUserSaldo: <%= cuSaldo != null ? cuSaldo.toString() : "<null>" %></p>
<hr/>
<p>Session id: <%= session.getId() %></p>
<p>Session creation time: <%= new java.util.Date(session.getCreationTime()) %></p>
<p>Session last accessed: <%= new java.util.Date(session.getLastAccessedTime()) %></p>
</body>
</html>