package modelo.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    // NOTE: Update the URL/USER/PASS to match your environment or wire this to a connection pool.
    private static final String URL = "jdbc:mysql://localhost:3306/wachiturro";
    private static final String USER = "root";
    private static final String PASS = "password";

    static {
        try {
            // Load MySQL driver if necessary (depends on JDBC driver and JDK)
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // Driver not found; runtime will fail when attempting connections if not provided
            System.err.println("JDBC Driver not found: " + e.getMessage());
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }
}
