import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnection {
    private static HikariDataSource dataSource;
    
    static {
        try {
            initializeDataSource();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to initialize database connection pool", e);
        }
    }
    
    private static void initializeDataSource() throws IOException {
        Properties props = new Properties();
        
        // Try to load from properties file, fallback to defaults
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input != null) {
                props.load(input);
            }
        } catch (IOException e) {
            System.out.println("Could not load database.properties, using defaults");
        }
        
        // Set default values if not found in properties file
        String url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/hospital_management");
        String username = props.getProperty("db.username", "root");
        String password = props.getProperty("db.password", "");
        String driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
        
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName(driver);
        
        // Connection pool settings
        config.setMaximumPoolSize(Integer.parseInt(props.getProperty("db.maxPoolSize", "10")));
        config.setMinimumIdle(Integer.parseInt(props.getProperty("db.minPoolSize", "2")));
        config.setConnectionTimeout(30000); // 30 seconds
        config.setIdleTimeout(600000); // 10 minutes
        config.setMaxLifetime(1800000); // 30 minutes
        
        // Additional MySQL specific settings
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.addDataSourceProperty("useLocalSessionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        
        dataSource = new HikariDataSource(config);
        
        // Test the connection
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("Database connection established successfully!");
        } catch (SQLException e) {
            System.err.println("Failed to establish database connection: " + e.getMessage());
            throw new RuntimeException("Database connection failed", e);
        }
    }
    
    public static Connection getConnection() throws SQLException {
        if (dataSource == null) {
            throw new SQLException("DataSource is not initialized");
        }
        return dataSource.getConnection();
    }
    
    public static void closeDataSource() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            System.out.println("Database connection pool closed");
        }
    }
    
    // Method to test database connectivity
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            System.err.println("Database connection test failed: " + e.getMessage());
            return false;
        }
    }
}