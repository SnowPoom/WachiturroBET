package modelo.dao;

import modelo.entidades.Billetera;
import modelo.entidades.UsuarioRegistrado;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BilleteraDAO {

    // Recarga la billetera usando PreparedStatement para evitar inyección SQL.
    public boolean recargarBilletera(double monto, UsuarioRegistrado usuario) {
        String sql = "UPDATE billetera SET saldo = saldo + ? WHERE idUsuario = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, monto);
            ps.setInt(2, usuario.getId());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Busca la billetera por idUsuario y devuelve el objeto Billetera completo.
    public Billetera buscarPorUsuario(int idUsuario) {
        String sql = "SELECT id, saldo, idUsuario FROM billetera WHERE idUsuario = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Billetera b = new Billetera();
                    b.setId(rs.getInt("id"));
                    b.setSaldo(rs.getDouble("saldo"));
                    b.setIdUsuario(rs.getInt("idUsuario"));
                    return b;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
