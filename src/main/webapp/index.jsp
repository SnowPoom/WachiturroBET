<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Este archivo redirige al inicializador de datos de prueba
    String path = request.getContextPath();
    // Llamamos al endpoint que crea/verifica datos y luego redirige al login
    response.sendRedirect(path + "/initTestData");
%>