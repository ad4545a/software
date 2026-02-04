package com.jewellery.erp.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class AppConfig {

    private static SessionFactory sessionFactory;
    private static final String DB_URL = "jdbc:h2:./data/jewellery;CIPHER=AES";
    private static final String DB_USER = "admin";
    private static final String DB_PASS = "filepwd userpwd";

    public static void initialize() {
        System.out.println("Initializing Application...");
        setupDatabase();
    }

    private static void setupDatabase() {
        try {
            // Ensure DB directory exists
            File dbDir = new File("data");
            if (!dbDir.exists()) {
                dbDir.mkdirs();
            }

            // 1. Initialize Schema & Default Data via JDBC
            try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

                // Create Schema Version Table
                try (Statement stmt = conn.createStatement()) {
                    stmt.execute("CREATE TABLE IF NOT EXISTS SCHEMA_VERSION (" +
                            "version INT PRIMARY KEY, " +
                            "applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
                }

                // Check Version
                int currentVersion = 0;
                try (Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT MAX(version) FROM SCHEMA_VERSION")) {
                    if (rs.next()) {
                        currentVersion = rs.getInt(1);
                    }
                }

                if (currentVersion < 1) {
                    System.out.println("Applying Schema Version 1...");

                    // Create Users Table (Foundation for Phase 1)
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute("CREATE TABLE IF NOT EXISTS USERS (" +
                                "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                                "username VARCHAR(50) UNIQUE NOT NULL, " +
                                "password_hash VARCHAR(255) NOT NULL, " +
                                "role VARCHAR(20) NOT NULL, " +
                                "active BOOLEAN DEFAULT TRUE, " +
                                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                                ")");
                    }

                    // Insert Default Admin
                    String adminHash = BCrypt.hashpw("admin123", BCrypt.gensalt(12));
                    try (PreparedStatement ps = conn.prepareStatement(
                            "INSERT INTO USERS (username, password_hash, role) VALUES (?, ?, ?)")) {
                        ps.setString(1, "admin");
                        ps.setString(2, adminHash);
                        ps.setString(3, "ADMIN");
                        ps.executeUpdate();
                        System.out.println("Default Admin User Created.");
                    }

                    // Update Schema Version
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute("INSERT INTO SCHEMA_VERSION (version) VALUES (1)");
                    }
                }
            }

            // 2. Setup Hibernate
            Configuration configuration = new Configuration();
            configuration.setProperty("hibernate.connection.driver_class", "org.h2.Driver");
            configuration.setProperty("hibernate.connection.url", DB_URL);
            configuration.setProperty("hibernate.connection.username", DB_USER);
            configuration.setProperty("hibernate.connection.password", DB_PASS);
            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
            configuration.setProperty("hibernate.show_sql", "true");
            configuration.setProperty("hibernate.hbm2ddl.auto", "validate"); // Changed from update to validate to
                                                                             // respect manual schema control

            sessionFactory = configuration.buildSessionFactory();
            System.out.println("Database Connection Established Successfully.");

        } catch (Exception e) {
            System.err.println("Database Initialization Failed: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
