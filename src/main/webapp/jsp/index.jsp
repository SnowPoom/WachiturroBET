<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>WachiturroBet - Home</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/styles.css">
</head>
<body>

   <header class="navbar">
        <div class="container nav-content">
            <a href="${pageContext.request.contextPath}/ListarEventosController?ruta=entrar" class="logo">
                <div class="logo-icon"> <img src="${pageContext.request.contextPath}/jsp/image.png" alt="icon"></div>
                <span>WachiturroBet</span>
            </a>

            <div class="search-wrapper">
                <svg class="icon search-icon-overlay" viewBox="0 0 24 24">
                    <circle cx="11" cy="11" r="8"></circle>
                    <line x1="21" x2="16.65" y1="21" y2="16.65"></line>
                </svg>
                <input type="text" class="input input-search" placeholder="Buscar eventos, equipos o ligas...">
            </div>

            <div class="flex-center gap-2">
                <a href="${pageContext.request.contextPath}/adminLogin" class="btn btn-ghost" 
                   style="color: #ec4899; border: 1px dashed rgba(236, 72, 153, 0.5); margin-right: 10px;" 
                   title="Switch to Admin Session">
                    <svg class="icon" viewBox="0 0 24 24"><path d="M16 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="8.5" cy="7" r="4"></circle><line x1="20" y1="8" x2="20" y2="14"></line><line x1="23" y1="11" x2="17" y2="11"></line></svg>
                    Modo Admin
                </a>
                <a href="${pageContext.request.contextPath}/recargarBilletera" class="btn btn-outline">Billetera</a>
                <a href="${pageContext.request.contextPath}/historial" class="btn btn-outline">Historial</a>
                 
                <% if (session.getAttribute("currentUser") == null) { %>
                    <a href="jsp/login.jsp" class="btn btn-outline">Ingresar</a>
                    <a href="jsp/register.jsp" class="btn btn-primary">Registro</a>
                <% } else { %>
                     <span class="text-purple">Hola, ${currentUser.nombre}</span>
                     <a href="${pageContext.request.contextPath}/LogoutController" class="btn btn-outline">Salir</a>
                <% } %>
            </div>
        </div>
    </header>

    <div class="container" style="padding-top: 2rem; padding-bottom: 2rem;">
        
        <div class="text-center mb-8">
            <h1 class="text-4xl flex-center gap-2 mb-4">
                <svg class="icon icon-lg" style="color: #eab308;" viewBox="0 0 24 24"><path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/><path d="M4 22h16"/><path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/><path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/><path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"/></svg>
                Apuestas en Vivo
            </h1>
        </div>

        <div class="card" style="background: var(--gradient-card);">
            <div class="card-content" style="padding-top: 1.5rem;">
                <div class="flex-between" style="flex-wrap: wrap; gap: 1rem;">
                    <div>
                        <h3 class="card-title" style="font-size: 1.25rem;">¡Únete!</h3>
                        <p class="card-desc">Crea tu cuenta y comienza a ganar hoy mismo</p>
                    </div>
                    <% if (session.getAttribute("currentUser") == null) { %>
                    <div class="flex-center gap-2">
                        <a href="jsp/login.jsp" class="btn btn-outline">Iniciar Sesión</a>
                        <a href="jsp/register.jsp" class="btn btn-primary">Registrarse Ahora</a>
                    </div>
                    <% } %>
                </div>
            </div>
        </div>

        <div class="grid-3 mb-8">
            <div class="card">
                <div class="card-content" style="padding-top: 1.5rem;">
                    <div class="flex-center" style="justify-content: flex-start; gap: 1rem;">
                        <div style="padding: 0.75rem; background: rgba(168, 85, 247, 0.2); border-radius: 0.5rem;">
                            <svg class="icon text-purple" viewBox="0 0 24 24"><polyline points="23 6 13.5 15.5 8.5 10.5 1 18"/><polyline points="17 6 23 6 23 12"/></svg>
                        </div>
                        <div>
                            <div class="text-2xl font-bold">250+</div>
                            <div class="card-desc">Eventos en Vivo</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="card">
                <div class="card-content" style="padding-top: 1.5rem;">
                    <div class="flex-center" style="justify-content: flex-start; gap: 1rem;">
                        <div style="padding: 0.75rem; background: rgba(236, 72, 153, 0.2); border-radius: 0.5rem;">
                            <svg class="icon" style="color: var(--color-pink);" viewBox="0 0 24 24"><path d="M6 9H4.5a2.5 2.5 0 0 1 0-5H6"/><path d="M18 9h1.5a2.5 2.5 0 0 0 0-5H18"/><path d="M4 22h16"/><path d="M10 14.66V17c0 .55-.47.98-.97 1.21C7.85 18.75 7 20.24 7 22"/><path d="M14 14.66V17c0 .55.47.98.97 1.21C16.15 18.75 17 20.24 17 22"/><path d="M18 2H6v7a6 6 0 0 0 12 0V2Z"/></svg>
                        </div>
                        <div>
                            <div class="text-2xl font-bold">$1M+</div>
                            <div class="card-desc">Pagado en Premios</div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="card">
                <div class="card-content" style="padding-top: 1.5rem;">
                    <div class="flex-center" style="justify-content: flex-start; gap: 1rem;">
                        <div style="padding: 0.75rem; background: rgba(59, 130, 246, 0.2); border-radius: 0.5rem;">
                            <svg class="icon" style="color: #60a5fa;" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                        </div>
                        <div>
                            <div class="text-2xl font-bold">24/7</div>
                            <div class="card-desc">Soporte Disponible</div>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <h2 class="text-2xl mb-4">Apuestas Disponibles</h2>
        
        <div class="grid-2">
            <c:forEach var="evento" items="${eventos}">
                <div class="card">
                    <div class="card-header">
                        <div class="flex-between mb-4">
                            <span class="badge badge-purple">${evento.categoria}</span>
                            <div class="flex-center gap-2 card-desc">
                                <svg class="icon" style="width: 1rem; height: 1rem;" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/><polyline points="12 6 12 12 16 14"/></svg>
                                <span>${evento.fechaFormateada}</span>
                            </div>
                        </div>
                        <h3 class="card-title">${evento.nombre}</h3>
                        <p class="card-desc">${evento.descripcion}</p>
                    </div>
                    
                    <div class="card-content">
                        <div class="grid-2" style="gap: 0.5rem;">
                            <a href="${pageContext.request.contextPath}/ListarEventosController?ruta=seleccionarEvento&id=${evento.id}" 
                               class="btn btn-outline" 
                               style="text-decoration: none; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 1rem; width: 100%;">
                                
                                <span>Ver</span>
                                <span class="text-purple font-bold">Ver detalles y cuotas</span>
                            </a>
                        </div>
                    </div>
                </div>
            </c:forEach>
            
            <c:if test="${empty eventos}">
                <div class="card" style="grid-column: 1 / -1;">
                    <div class="card-content text-center">
                        <h3>No hay eventos disponibles por el momento.</h3>
                        <p>Vuelve más tarde para ver nuevos partidos.</p>
                    </div>
                </div>
            </c:if>
        </div>
    </div>
    <script>
    (function() {
        // Solo intentamos inicializar si no estamos ya en el proceso
        // Hacemos una petición silenciosa (fetch)
        fetch('${pageContext.request.contextPath}/initTestData')
            .then(response => {
                console.log("Datos verificados/inicializados");
                // Opcional: Recargar si la lista estaba vacía
            })
            .catch(error => console.log("Error inicializando datos", error));
    })();
</script>

</body>
</html>