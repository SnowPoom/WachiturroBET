package modelo.dao;

import modelo.entidades.Movimiento;
import modelo.entidades.TipoMovimiento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class MovimientoDAO {

    // Inserta un movimiento en la tabla movimientos.
    public boolean guardarMovimiento(Movimiento mov) {
        String sql = "INSERT INTO movimiento (idBilletera, tipo, monto, fecha) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, mov.getIdBilletera());
            // Guardamos el nombre del enum como String para simplicidad
            ps.setString(2, mov.getTipo() != null ? mov.getTipo().name() : null);
            ps.setDouble(3, mov.getMonto());
            ps.setTimestamp(4, mov.getFecha() != null ? Timestamp.valueOf(mov.getFecha()) : null);

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
