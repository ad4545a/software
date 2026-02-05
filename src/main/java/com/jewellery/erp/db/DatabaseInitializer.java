package com.jewellery.erp.db;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseInitializer {

    private static final String DB_URL = com.jewellery.erp.config.ConfigLoader.get("db.url");
    private static final String DB_USER = com.jewellery.erp.config.ConfigLoader.get("db.user");
    private static final String DB_PASS = com.jewellery.erp.config.ConfigLoader.get("db.password");

    public static void initialize() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)) {

            // 1. Schema Version Table (Always runs)
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("CREATE TABLE IF NOT EXISTS SCHEMA_VERSION (" +
                        "version INT PRIMARY KEY, " +
                        "applied_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            }

            // 2. Check current version
            int currentVersion = 0;
            try (Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery("SELECT MAX(version) FROM SCHEMA_VERSION")) {
                if (rs.next()) {
                    currentVersion = rs.getInt(1);
                }
            }

            // 3. Apply Migrations sequentially
            if (currentVersion < 1) {
                applyVersion1(conn);
                currentVersion = 1;
            }

            if (currentVersion < 2) {
                applyVersion2(conn);
                currentVersion = 2;
            }

            if (currentVersion < 3) {
                applyVersion3(conn);
                currentVersion = 3;
            }

        } catch (Exception e) {
            System.err.println("DB Initialization Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Database Init Failed", e);
        }
    }

    private static void applyVersion1(Connection conn) throws Exception {
        System.out.println("Applying Schema Version 1 (Users & Admin)...");

        // Create USERS Table
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

        // Insert Default Admin with Random Password (First Run Only)
        String tempPassword = com.jewellery.erp.util.SecurePasswordGenerator.generate();
        String adminHash = BCrypt.hashpw(tempPassword, BCrypt.gensalt(12));

        try (PreparedStatement ps = conn.prepareStatement(
                "MERGE INTO USERS (username, password_hash, role) KEY(username) VALUES (?, ?, ?)")) {
            ps.setString(1, "admin");
            ps.setString(2, adminHash);
            ps.setString(3, "ADMIN");
            ps.executeUpdate();

            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.println("  ⚠️  INITIAL ADMIN PASSWORD (SAVE THIS IMMEDIATELY): ");
            System.out.println("  Username: admin");
            System.out.println("  Password: " + tempPassword);
            System.out.println("  ⚡ CHANGE THIS PASSWORD ON FIRST LOGIN ⚡");
            System.out.println("═══════════════════════════════════════════════════════════════");
        }

        // Update Schema Version
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("MERGE INTO SCHEMA_VERSION (version) KEY(version) VALUES (1)");
        }
    }

    private static void applyVersion2(Connection conn) throws Exception {
        System.out.println("Applying Schema Version 2 (Login Audit)...");

        // Create LOGIN_AUDIT Table
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS LOGIN_AUDIT (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                    "user_id BIGINT, " +
                    "login_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                    "success BOOLEAN, " +
                    "machine_id VARCHAR(255) " +
                    ")");
        }

        // Update Schema Version
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("MERGE INTO SCHEMA_VERSION (version) KEY(version) VALUES (2)");
        }
    }

    private static void applyVersion3(Connection conn) throws Exception {
        System.out.println("Applying Schema Version 3 (Phase 2: Master Data)...");

        // 1. COMPANY Table
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS COMPANY (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(200) UNIQUE NOT NULL, " +
                    "address VARCHAR(500), " +
                    "phone VARCHAR(20), " +
                    "gst_number VARCHAR(15), " +
                    "active BOOLEAN DEFAULT TRUE NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                    "updated_at TIMESTAMP" +
                    ")");
        }

        // 2. USER_PROFILE Table
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS USER_PROFILE (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                    "user_id BIGINT UNIQUE NOT NULL, " +
                    "full_name VARCHAR(100) NOT NULL, " +
                    "mobile VARCHAR(15), " +
                    "assigned_counter BIGINT, " +
                    "active BOOLEAN DEFAULT TRUE NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                    "updated_at TIMESTAMP" +
                    ")");
        }

        // 3. METAL Table
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS METAL (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(50) UNIQUE NOT NULL, " +
                    "code VARCHAR(10) UNIQUE NOT NULL, " +
                    "active BOOLEAN DEFAULT TRUE NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                    "updated_at TIMESTAMP" +
                    ")");
        }

        // 4. PURITY Table
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS PURITY (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                    "metal_id BIGINT NOT NULL, " +
                    "name VARCHAR(50) NOT NULL, " +
                    "percentage DOUBLE NOT NULL, " +
                    "active BOOLEAN DEFAULT TRUE NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                    "updated_at TIMESTAMP, " +
                    "CONSTRAINT uk_purity_metal_name UNIQUE (metal_id, name), " +
                    "FOREIGN KEY (metal_id) REFERENCES METAL(id)" +
                    ")");
        }

        // 5. ITEM Table
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS ITEM (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(100) NOT NULL, " +
                    "metal_id BIGINT NOT NULL, " +
                    "purity_id BIGINT, " +
                    "wastage_percent DOUBLE DEFAULT 0.0 NOT NULL, " +
                    "active BOOLEAN DEFAULT TRUE NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                    "updated_at TIMESTAMP, " +
                    "CONSTRAINT uk_item_name_metal_purity UNIQUE (name, metal_id, purity_id), " +
                    "FOREIGN KEY (metal_id) REFERENCES METAL(id), " +
                    "FOREIGN KEY (purity_id) REFERENCES PURITY(id)" +
                    ")");
        }

        // 6. STONE Table
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS STONE (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(100) UNIQUE NOT NULL, " +
                    "unit VARCHAR(20) NOT NULL, " +
                    "rate_type VARCHAR(20) NOT NULL, " +
                    "active BOOLEAN DEFAULT TRUE NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                    "updated_at TIMESTAMP" +
                    ")");
        }

        // 7. TAX Table
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS TAX (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(100) UNIQUE NOT NULL, " +
                    "percentage DOUBLE NOT NULL, " +
                    "is_default BOOLEAN DEFAULT FALSE NOT NULL, " +
                    "active BOOLEAN DEFAULT TRUE NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                    "updated_at TIMESTAMP" +
                    ")");
        }

        // 8. COUNTER Table
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS COUNTER (" +
                    "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
                    "name VARCHAR(100) UNIQUE NOT NULL, " +
                    "location VARCHAR(200), " +
                    "active BOOLEAN DEFAULT TRUE NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, " +
                    "updated_at TIMESTAMP" +
                    ")");
        }

        // Update Schema Version
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("MERGE INTO SCHEMA_VERSION (version) KEY(version) VALUES (3)");
        }

        System.out.println("Phase 2 Master Data tables created successfully.");
    }
}
