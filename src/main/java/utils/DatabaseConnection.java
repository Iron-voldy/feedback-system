package utils;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static String DB_URL = "jdbc:sqlserver://DESKTOP-3QDRT02;instanceName=SQLEXPRESS;database=test1;encrypt=true;trustServerCertificate=true";
    private static String DB_USER = "sa";
    private static String DB_PASSWORD = "1234";

    public static Connection getConnection() throws SQLException {

        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Driver not found: " + e.getMessage());
        }

        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

}
