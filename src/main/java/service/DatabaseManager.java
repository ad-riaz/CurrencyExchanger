package service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {
    private static DatabaseManager instance;
    public Connection connection;
    private static final String dbSource = PropsReader.getProperty("dbSource");

    private DatabaseManager() {
        // Loading JDBC driver
        try {
            DbDriverLoader.load();
        } catch(Exception e) {
            e.printStackTrace();
        };
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseManager.class) {
                if (instance == null) {
                    instance = new DatabaseManager();
                }
            }
        }
        return instance;
    }

    public void openConnection() throws SQLException {
        // Database connection initialisation
        connection = DriverManager.getConnection(dbSource);
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
