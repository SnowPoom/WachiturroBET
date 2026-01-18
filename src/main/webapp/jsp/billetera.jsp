<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>WachiturroBet - Cartera</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/jsp/styles.css">
    <style>
        /* Estilos específicos para las Tabs de Wallet */
        .tab-content { display: none; }
        .tab-content.active { display: block; }
        
        .tabs-list {
            display: grid; 
            grid-template-columns: 1fr 1fr;
            background: rgba(15, 23, 42, 0.5);
            padding: 0.25rem;
            border-radius: 0.5rem;
            margin-bottom: 1.5rem;
        }
        
        .tab-btn {
            background: transparent; 
            border: none;
            color: white;
            padding: 0.5rem;
            border-radius: 0.25rem;
            cursor: pointer;
            font-weight: 500;
            transition: all 0.2s;
        }
        
        .tab-btn.active {
            background-color: rgba(168, 85, 247, 0.2); 
            color: #d8b4fe;
        }
        /* Mensajes de resultado */
        .result-ok { background: #dcfce7; color: #14532d; padding: 0.75rem; border-radius: 6px; margin-bottom: 1rem; }
        .result-err { background: #fee2e2; color: #7f1d1d; padding: 0.75rem; border-radius: 6px; margin-bottom: 1rem; }
    </style>
</head>
<body>

    <header class="navbar">
        <div class="container nav-content">
            <a href="index.jsp" class="logo">
                <div class="logo-icon"><img src="${pageContext.request.contextPath}/jsp/image.png" alt="icon"></div>
                <span>WachiturroBet</span>
            </a>
            
            <div class="flex-center gap-2">
                <a href="billetera.jsp" class="btn btn-ghost" style="background-color: rgba(168, 85, 247, 0.2);" title="Cartera">
                    <svg class="icon" viewBox="0 0 24 24"><path d="M21 12V7H5a2 2 0 0 1 0-4h14v4"/><path d="M3 5v14a2 2 0 0 0 2 2h16v-5"/><path d="M18 12a2 2 0 0 0 0 4h4v-4Z"/></svg>
                </a>
                <a href="profile.jsp" class="btn btn-ghost" title="Perfil">
                    <svg class="icon" viewBox="0 0 24 24"><path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                </a>
            </div>
        </div>
    </header>

    <div class="container" style="padding-top: 2rem; padding-bottom: 2rem;">
        
        <div class="text-center mb-8">
            <h1 class="text-4xl flex-center gap-2 mb-2">
                <svg class="icon" style="width: 2.5rem; height: 2.5rem; color: var(--color-purple);" viewBox="0 0 24 24"><path d="M21 12V7H5a2 2 0 0 1 0-4h14v4"/><path d="M3 5v14a2 2 0 0 0 2 2h16v-5"/><path d="M18 12a2 2 0 0 0 0 4h4v-4Z"/></svg>
                Mi Cartera
            </h1>
            <p class="card-desc">Gestiona tus fondos y transacciones</p>
        </div>

        <%-- LÓGICA AGREGADA: Manejo de mensajes Flash (Post-Redirect-Get) --%>
        <%
            // 1. Verificar si hay mensajes guardados en sesion por el Controller
            String flashStatus = (String) session.getAttribute("flash_status");
            
            if (flashStatus != null) {
                // 2. Pasar estos datos al request actual para que los fragments funcionen
                request.setAttribute("status", flashStatus);
                request.setAttribute("message", session.getAttribute("flash_message"));
                request.setAttribute("operacion", session.getAttribute("flash_operacion"));
                request.setAttribute("monto", session.getAttribute("flash_monto"));
                request.setAttribute("usuarioName", session.getAttribute("flash_usuarioName"));
                
                // 3. LIMPIAR la sesión para que al recargar la página no aparezca el mensaje de nuevo
                session.removeAttribute("flash_status");
                session.removeAttribute("flash_message");
                session.removeAttribute("flash_operacion");
                session.removeAttribute("flash_monto");
                session.removeAttribute("flash_usuarioName");
            }
            
            // Variable local para verificar qué status usar
            String status = (String) request.getAttribute("status");
        %>

        <%-- Mostrar resultado según status --%>
        <% if (status != null && "OK".equals(status)) { %>
            <jsp:include page="/jsp/MensajeConfirmacionBilletera.jsp" />
        <% } else if (status != null) { %>
            <jsp:include page="/jsp/MensajeErrorBilletera.jsp" />
        <% } %>

        <%-- Variables de Sesión (Usuario y Saldo) --%>
        <%
            Object sessionUserObj = session.getAttribute("currentUser");
            Double sessionSaldoObj = null;
            String sessionUserName = null;
            int sessionUserId = 0;
            if (session.getAttribute("currentUserSaldo") != null) {
                try { sessionSaldoObj = (Double) session.getAttribute("currentUserSaldo"); } catch (Exception ex) { sessionSaldoObj = null; }
            }
            if (sessionUserObj != null) {
                modelo.entidades.UsuarioRegistrado su = (modelo.entidades.UsuarioRegistrado) sessionUserObj;
                sessionUserName = su.getNombre();
                sessionUserId = su.getId();
            }
        %>

        <div class="card" style="background: linear-gradient(135deg, rgba(168, 85, 247, 0.2), rgba(236, 72, 153, 0.2)); border-color: rgba(168, 85, 247, 0.3);">
            <div class="card-content text-center" style="padding-top: 2rem; padding-bottom: 2rem;">
                <div class="card-desc" style="color: #e2e8f0;">Saldo Disponible</div>
                <div style="font-size: 3.5rem; font-weight: bold; margin: 1rem 0;">$<%= sessionSaldoObj != null ? sessionSaldoObj : 2450.0 %></div>
                <div class="flex-center gap-4">
                    <span class="badge badge-green">
                        <svg class="icon" style="width: 0.75rem; margin-right: 0.25rem;" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/><path d="m16 12-4-4-4 4"/><path d="M12 16V8"/></svg>
                        +$750 este mes
                    </span>
                    <span class="badge badge-purple" style="color: #93c5fd; background: rgba(59, 130, 246, 0.2); border-color: rgba(59, 130, 246, 0.3);">
                        Verificado
                    </span>
                </div>
            </div>
        </div>

        <div class="card">
            <div class="card-content" style="padding-top: 1.5rem;">
                <div class="tabs-list">
                    <button onclick="switchTab('deposit')" id="btn-deposit" class="tab-btn active">Depositar</button>
                    <button onclick="switchTab('withdraw')" id="btn-withdraw" class="tab-btn">Retirar</button>
                </div>

                <div id="tab-deposit" class="tab-content active">
                    <div class="text-center mb-4">
                        <h3 class="text-2xl">Agregar Fondos</h3>
                        <p class="card-desc">Deposita dinero a tu cuenta de forma segura</p>
                    </div>
                    <form method="post" action="${pageContext.request.contextPath}/recargarBilletera">
                        <!-- Agregado: indicar la ruta al controlador para que procese la recarga -->
                        <input type="hidden" name="ruta" value="recargar" />
                        <div class="form-group">
                            <label class="label">Cantidad a Depositar</label>
                            <input type="number" step="0.01" name="monto" placeholder="100" class="input" required
                                   />
                        </div>
                        
                        <button type="submit" class="btn btn-primary btn-full">
                            <svg class="icon" style="margin-right: 0.5rem;" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/><path d="m16 12-4-4-4 4"/><path d="M12 16V8"/></svg>
                            Depositar Ahora
                        </button>
                    </form>
                </div>
			
				<div id="tab-withdraw" class="tab-content">
				    <div class="text-center mb-4">
				        <h3 class="text-2xl">Retirar Fondos</h3>
				        <p class="card-desc">Retira tus ganancias de forma segura</p>
				    </div>
				    
				    <form method="post" action="${pageContext.request.contextPath}/retirarFondos">
				        
				        <input type="hidden" name="ruta" value="retirar">
				
				        <% if (sessionUserObj != null) { %>
				            <input type="hidden" name="usuarioId" value="<%= sessionUserId %>" />
				        <% } %>
				
				        <div class="form-group">
				            <label class="label">Cantidad a Retirar</label>
				            <input type="number" step="0.01" name="monto" placeholder="100" class="input" required
                                   />
				            <div style="font-size: 0.8rem; color: var(--text-muted); margin-top: 0.25rem;">
				                Disponible: $<%= sessionSaldoObj != null ? sessionSaldoObj : 0.0 %>
				            </div>
				        </div>
				        
				        <button type="submit" class="btn btn-primary btn-full">
				            <svg class="icon" style="margin-right: 0.5rem;" viewBox="0 0 24 24">
				                <circle cx="12" cy="12" r="10"/><path d="m16 12-4 4-4-4"/><path d="M12 8v8"/>
				            </svg>
				            Retirar Ahora
				        </button>
				    </form>
				</div>

            </div>
        </div>

        <div class="card">
            <div class="card-header">
                <h3 class="card-title">Historial de Transacciones</h3>
                <p class="card-desc">Todas tus transacciones recientes</p>
            </div>
            <div class="card-content">
                <div style="display: flex; flex-direction: column; gap: 1rem;">
                    <div class="flex-between" style="background: rgba(15, 23, 42, 0.5); padding: 1rem; border-radius: 0.5rem;">
                        <div class="flex-center gap-4">
                            <div style="padding: 0.5rem; background: rgba(34, 197, 94, 0.2); border-radius: 0.5rem;">
                                <svg class="icon" style="color: #4ade80;" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/><path d="m16 12-4-4-4 4"/><path d="M12 16V8"/></svg>
                            </div>
                            <div>
                                <div style="font-weight: 500;">Tarjeta</div>
                                <div class="card-desc" style="font-size: 0.8rem;">22 Nov 2024, 10:30</div>
                            </div>
                        </div>
                        <div class="text-right">
                            <div style="color: #4ade80;">+500</div>
                            <span class="badge badge-purple" style="font-size: 0.7rem;">Completado</span>
                        </div>
                    </div>
                    <div class="flex-between" style="background: rgba(15, 23, 42, 0.5); padding: 1rem; border-radius: 0.5rem;">
                        <div class="flex-center gap-4">
                            <div style="padding: 0.5rem; background: rgba(239, 68, 68, 0.2); border-radius: 0.5rem;">
                                <svg class="icon" style="color: #f87171;" viewBox="0 0 24 24"><circle cx="12" cy="12" r="10"/><path d="m16 12-4 4-4-4"/><path d="M12 8v8"/></svg>
                            </div>
                            <div>
                                <div style="font-weight: 500;">Transferencia</div>
                                <div class="card-desc" style="font-size: 0.8rem;">21 Nov 2024, 15:45</div>
                            </div>
                        </div>
                        <div class="text-right">
                            <div style="color: #f87171;">-200</div>
                            <span class="badge badge-purple" style="font-size: 0.7rem;">Completado</span>
                        </div>
                    </div>
                </div>
            </div>
        </div>

    </div>

    <script>
        function switchTab(tabName) {
            document.querySelectorAll('.tab-content').forEach(el => el.classList.remove('active'));
            document.querySelectorAll('.tab-btn').forEach(el => el.classList.remove('active'));
            document.getElementById('tab-' + tabName).classList.add('active');
            document.getElementById('btn-' + tabName).classList.add('active');
        }
    </script>
</body>
</html>