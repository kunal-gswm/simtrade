package com.project.trading.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Thread-safe centralized database connection management backed by a HikariCP connection pool.
 */
public class DBConnection {

    private static final HikariDataSource dataSource;

    static {
        Properties props = new Properties();
        try (InputStream in = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            System.err.println("[DBConnection] Warning: Could not read db.properties: " + e.getMessage());
        }

        String envUrl = System.getenv("DB_URL");
        String envUser = System.getenv("DB_USER");
        String envPass = System.getenv("DB_PASSWORD");

        String url = (envUrl != null && !envUrl.isBlank()) ? envUrl : props.getProperty("db.url");
        String user = (envUser != null && !envUser.isBlank()) ? envUser : props.getProperty("db.user");
        String pass = (envPass != null && !envPass.isBlank()) ? envPass : props.getProperty("db.password");

        if (url == null || url.isBlank() || user == null || user.isBlank()) {
            throw new IllegalStateException(
                "Database configuration is incomplete. " +
                "Explicit credentials are required: configure db.url, db.user, and db.password in db.properties, " +
                "or set DB_URL, DB_USER, and DB_PASSWORD environment variables."
            );
        }
        if (pass == null) {
            pass = "";
        }

        int maxPoolSize = Integer.parseInt(props.getProperty("db.pool.maxSize", "15"));
        int minIdle = Integer.parseInt(props.getProperty("db.pool.minIdle", "2"));
        long connTimeout = Long.parseLong(props.getProperty("db.pool.connectionTimeoutMs", "10000"));
        long idleTimeout = Long.parseLong(props.getProperty("db.pool.idleTimeoutMs", "30000"));

        HikariConfig config = new HikariConfig();
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(pass);
        config.setMaximumPoolSize(maxPoolSize);
        config.setMinimumIdle(minIdle);
        config.setConnectionTimeout(connTimeout);
        config.setIdleTimeout(idleTimeout);
        config.setMaxLifetime(1800000); // 30 minutes
        config.setLeakDetectionThreshold(5000); // 5 seconds leak detection threshold
        config.setPoolName("SimTrade-HikariPool");

        // Performance optimizations for MySQL Connector/J
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        dataSource = new HikariDataSource(config);
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static void closePool() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    private DBConnection() {
    }
}
