package config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static Connection cn;
    // URL para MySQL 8.0 y WampServer
    private static final String DRIVER = "com.mysql.cj.jdbc.Driver"; 
    private static final String URL = "jdbc:mysql://localhost:3308/controldeturnos?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = ""; // Por defecto en Wamp va vacío

    public static Connection conectar() {
        try {
            if (cn == null || cn.isClosed()) {
                Class.forName(DRIVER);
                cn = DriverManager.getConnection(URL, USER, PASS);
            }
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Error en la conexión: " + e.getMessage());
        }
        return cn;
    }
}