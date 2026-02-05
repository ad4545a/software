package com.jewellery.erp.test.phase2;

import com.jewellery.erp.config.AppConfig;
import com.jewellery.erp.model.UserRole;
import com.jewellery.erp.security.SecurityContext;
import com.jewellery.erp.security.UserSession;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;

/**
 * Base class for Phase 2 backend tests.
 * Provides:
 * - Clean database before each test
 * - Fake admin session for permission-free testing
 * - Transaction utilities
 */
public abstract class TestBase {

    @BeforeAll
    public static void globalSetup() {
        // Clean DB file
        File dbFile = new File("./data/jewellery.mv.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }

        // Initialize schema to Version 3
        AppConfig.initialize();
    }

    @BeforeEach
    public void setupFakeAdminSession() {
        // Inject fake authenticated ADMIN session - no permission failures
        UserSession fakeAdmin = new UserSession(999L, "test-admin", UserRole.ADMIN, "test-machine");
        SecurityContext.setSession(fakeAdmin);
    }

    @AfterEach
    public void tearDown() {
        // Clear security context
        SecurityContext.clear();

        // Clean all master data tables for next test
        cleanMasterTables();
    }

    protected void cleanMasterTables() {
        Transaction tx = null;
        try (Session session = AppConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Delete in reverse FK order
            session.createMutationQuery("DELETE FROM Item").executeUpdate();
            session.createMutationQuery("DELETE FROM Purity").executeUpdate();
            session.createMutationQuery("DELETE FROM Metal").executeUpdate();
            session.createMutationQuery("DELETE FROM Stone").executeUpdate();
            session.createMutationQuery("DELETE FROM Tax").executeUpdate();
            session.createMutationQuery("DELETE FROM UserProfile").executeUpdate();
            session.createMutationQuery("DELETE FROM Counter").executeUpdate();
            session.createMutationQuery("DELETE FROM Company").executeUpdate();

            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive())
                tx.rollback();
            e.printStackTrace();
            throw new RuntimeException("Cleanup failed", e);
        }
    }
}
