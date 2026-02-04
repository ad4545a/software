package com.jewellery.erp.test.phase1.auth;

import com.jewellery.erp.config.AppConfig;
import com.jewellery.erp.model.UserRole;
import com.jewellery.erp.repository.LoginAuditRepository;
import com.jewellery.erp.security.AuthException;
import com.jewellery.erp.security.SecurityContext;
import com.jewellery.erp.security.UserSession;
import com.jewellery.erp.service.AuthService;
import org.hibernate.Session;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class AuthTest {

    private static AuthService authService;

    @BeforeAll
    public static void setup() {
        File dbFile = new File("./data/jewellery.mv.db");
        if (dbFile.exists())
            dbFile.delete();

        AppConfig.initialize(); // Creates Admin user
        authService = new AuthService();
    }

    @Test
    public void testValidLogin() {
        UserSession session = authService.login("admin", "admin123");
        assertNotNull(session);
        assertEquals(UserRole.ADMIN, session.getRole());
        assertTrue(SecurityContext.isAuthenticated());

        // Verify Audit
        try (Session s = AppConfig.getSessionFactory().openSession()) {
            Long count = s.createQuery("SELECT count(l) FROM LoginAudit l WHERE l.success = true", Long.class)
                    .uniqueResult();
            assertTrue(count > 0, "Audit log should record success");
        }
    }

    @Test
    public void testInvalidLogin() {
        assertThrows(AuthException.class, () -> authService.login("admin", "wrong"));
        assertThrows(AuthException.class, () -> authService.login("ghost", "123"));

        // Context should be empty (or at least previous session cleared if we logged
        // out,
        // but here we didn't logout from previous test.
        // Ideally tests should be isolated, but static DB makes it hard.
        // We check Audit for failure.
        try (Session s = AppConfig.getSessionFactory().openSession()) {
            Long count = s.createQuery("SELECT count(l) FROM LoginAudit l WHERE l.success = false", Long.class)
                    .uniqueResult();
            assertTrue(count > 0, "Audit log should record failure");
        }
    }
}
