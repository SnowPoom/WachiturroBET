Recarga de Billetera - Módulo (MVC + JPA)

Resumen
- Implementación de un módulo de "Recarga de Billetera" siguiendo MVC y patrón DAO.
- Persistencia con JPA (EclipseLink) usando las entidades anotadas.

Archivos añadidos / modificados
- Entidades:
  - `src/main/java/modelo/entidades/Movimiento.java` (nuevo: @Entity con FK a UsuarioRegistrado, tipo, monto, fecha)
- DAOs (JPA):
  - `src/main/java/modelo/dao/JPAUtil.java` (util para EntityManager)
  - `src/main/java/modelo/dao/IBilleteraDAO.java` (interface)
  - `src/main/java/modelo/dao/IMovimientoDAO.java` (interface)
  - `src/main/java/modelo/dao/jpa/BilleteraJPADAO.java` (implementa IBilleteraDAO usando EntityManager)
  - `src/main/java/modelo/dao/jpa/MovimientoJPADAO.java` (implementa IMovimientoDAO usando EntityManager)
- Controlador (Servlet):
  - `src/main/java/controlador/RecargarBilleteraController.java` (servlet mapeado en `/recargarBilletera`, implementa doGet y doPost)
- Vistas (Mocks consola):
  - `src/main/java/vista/BilleteraView.java`
  - `src/main/java/vista/MensajeConfirmacionBilletera.java`
  - `src/main/java/vista/MensajeErrorBilletera.java`
- Configuración:
  - `src/main/java/META-INF/persistence.xml` (se añadió `modelo.entidades.Movimiento` a la unidad de persistencia)

Asunciones
- La unidad de persistencia se llama `WachiturroBET` (ya presente en `persistence.xml`).
- Las tablas y columnas existen en la base de datos; `eclipselink.ddl-generation` está desactivado por seguridad.
- El servlet se desplegará en un contenedor Jakarta/Servlet compatible (Tomcat 10+ / Jakarta Servlet 6).

Cómo probar localmente
1) Actualiza las credenciales y URL en `src/main/java/META-INF/persistence.xml` si tu BD tiene otro nombre/usuario/clave.

2) Compilar y construir el WAR con Maven:

```bash
cd C:\Users\SnowPoom\eclipse-workspace\WachiturroBET
mvn -DskipTests=true package
```

3) Desplegar el WAR resultante (`target/WachiturroBET.war`) en Tomcat 10 (o servidor Jakarta compatible). Asegúrate de usar Tomcat 10+.

4) Acceder al formulario en el navegador:

- URL (ejemplo): http://localhost:8080/WachiturroBET/recargarBilletera

5) Flujo de prueba sencillo (curl):

```bash
# Mostrar formulario
curl http://localhost:8080/WachiturroBET/recargarBilletera

# Ejecutar recarga (reemplaza usuarioId y monto)
curl -X POST -d "usuarioId=1&monto=50.0" http://localhost:8080/WachiturroBET/recargarBilletera
```

Notas de calidad y siguientes pasos
- Se usaron vistas mock que imprimen en consola; si deseas integrarlas con JSP/HTML, puedo mover la salida al dispatcher/forward.
- No se agregaron tests unitarios; puedo añadir un test JUnit con un EntityManager en memoria (H2) si lo deseas.
- Recomiendo mover `persistence.xml` a `src/main/resources/META-INF/persistence.xml` si Maven no lo empaqueta correctamente desde `src/main/java`.

Contacto
- Si quieres que ejecute la compilación y corrija errores detectados, dime y la ejecuto (necesito permiso para correr `mvn package`).
