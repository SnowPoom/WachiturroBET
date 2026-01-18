<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    // Este archivo no muestra nada.
    // Su único trabajo es empujarte hacia el Controlador.
    String path = request.getContextPath();
    response.sendRedirect(path + "/ListarEventosController?ruta=entrar");
%>