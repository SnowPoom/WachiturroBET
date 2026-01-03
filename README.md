Proyecto WachiturroBET — Backend DAO (Muestras)

Resumen
-------
Este proyecto contiene implementaciones de ejemplo (paquetes `modelo.entidades` y `modelo.dao`) para un sistema de apuestas que sigue el patrón DAO.

Qué incluí
-----------
- Enums: `TipoMovimiento`, `EstadoApuesta`, `TipoCategoria`.
- Entidades: `Usuario`, `UsuarioRegistrado`, `Billetera`, `Movimiento`, `Evento`, `Apuesta`.
- DAOs:
  - `BilleteraDAO` con métodos `recargarBilletera(double monto, UsuarioRegistrado usuario)` y `buscarPorUsuario(int idUsuario)`.
  - `MovimientoDAO` con método `guardarMovimiento(Movimiento mov)`.
- Utilidad de conexión: `modelo.dao.DBUtil` (modifica URL/USER/PASS según tu entorno).
- Demo: `modelo.test.DAODemo` con un flujo de ejemplo: buscar billetera, recargar y registrar movimiento.

Notas de configuración
----------------------
- Actualiza las credenciales y URL de la base de datos en `modelo.dao.DBUtil`.
- El proyecto usa MySQL (dependencia `mysql-connector-j` ya está en `pom.xml`).

Esquema de tablas esperado (ejemplo mínimo)
------------------------------------------
-- Tabla billetera
CREATE TABLE billetera (
  id INT AUTO_INCREMENT PRIMARY KEY,
  saldo DOUBLE NOT NULL DEFAULT 0,
  idUsuario INT NOT NULL
);

-- Tabla movimiento
CREATE TABLE movimiento (
  id INT AUTO_INCREMENT PRIMARY KEY,
  idBilletera INT NOT NULL,
  tipo VARCHAR(50),
  monto DOUBLE NOT NULL,
  fecha DATETIME,
  FOREIGN KEY (idBilletera) REFERENCES billetera(id)
);

Consideraciones y mejoras sugeridas
----------------------------------
- Actualmente `recargarBilletera` y `guardarMovimiento` no comparten la misma transacción. Para garantizar atomicidad (saldo + movimiento), encapsula ambos en una transacción única o usa un servicio que maneje la transacción.
- Validaciones adicionales: comprobar que `usuario` existe, que `monto` sea positivo, manejo de concurrencia en actualizaciones de saldo.
- Production: use un pool de conexiones (HikariCP, Apache DBCP) en lugar de `DriverManager`.

Cómo ejecutar
-------------
- Desde Eclipse/IDE: importa como proyecto Maven, asegúrate de tener la base de datos configurada y ejecuta la clase `modelo.test.DAODemo` como aplicación Java.
- Desde línea de comandos: compila con Maven y ejecuta desde el IDE o agrega el plugin `exec-maven-plugin` para ejecutar el `main` desde `mvn`.

Archivos creados
----------------
- modelo/entidades: `TipoMovimiento.java`, `EstadoApuesta.java`, `TipoCategoria.java`, `Usuario.java`, `UsuarioRegistrado.java`, `Billetera.java`, `Movimiento.java`, `Evento.java`, `Apuesta.java`
- modelo/dao: `DBUtil.java`, `BilleteraDAO.java`, `MovimientoDAO.java`
- modelo/test: `DAODemo.java`

Contacto
-------
Si quieres que integre transacciones, validaciones o pruebas unitarias (JUnit), dime y lo implemento.
