package com.jewellery.erp.test;

import com.jewellery.erp.config.AppConfig;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class FoundationTest {

    @Test
    public void testFoundationAndSchema() {
        // Trigger Application Initialization (Creates DB, Schema, Admin)
        assertDoesNotThrow(() -> AppConfig.initialize(), "AppConfig initialization should not fail");

        // Verify File Exists
        File dbFile = new File("./data/jewellery.mv.db");
        assertTrue(dbFile.exists(), "Database file should exist");

        // Verify Schema Content via JDBC
        String jdbcUrl = "jdbc:h2:./data/jewellery;CIPHER=AES";
        String user = "admin";
        String pass = "filepwd userpwd";

        assertDoesNotThrow(() -> {
            try (Connection conn = DriverManager.getConnection(jdbcUrl, user, pass)) {

                // 1. Check Schema Version
                try (Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT version FROM SCHEMA_VERSION WHERE version = 1")) {
                    assertTrue(rs.next(), "Schema Version 1 should exist");
                }

                // 2. Check Admin User
                try (Statement stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT username, role FROM USERS WHERE username = 'admin'")) {
                    assertTrue(rs.next(), "Admin user should exist");
                    assertEquals("ADMIN", rs.getString("role"), "Admin role should be ADMIN");
                }
            }
        });
    }
}
