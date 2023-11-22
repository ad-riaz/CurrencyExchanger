package service;

import java.sql.Connection;
import java.sql.SQLException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseManager {
    private static DatabaseManager instance;
    private static final String dbSource = PropertiesReader.getProperty("dbSource");
    private HikariDataSource dataSource;


    private DatabaseManager() {
        // Loading JDBC driver
        try {
            DbDriverLoader.load();
        } catch(Exception e) {
            e.printStackTrace();
        };

        // Initializing HikariCP connection pool
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(dbSource);
        dataSource = new HikariDataSource(config);
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

    public synchronized Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public synchronized void closeConnection(Connection connection) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}
