package com.jewellery.erp.test.phase1.authorization;

import com.jewellery.erp.model.Feature;
import com.jewellery.erp.model.UserRole;
import com.jewellery.erp.security.AuthException;
import com.jewellery.erp.security.SecurityContext;
import com.jewellery.erp.security.UserSession;
import com.jewellery.erp.service.AuthorizationService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RbacTest {

    private final AuthorizationService authzService = new AuthorizationService();

    @AfterEach
    public void cleanup() {
        SecurityContext.clear();
    }

    @Test
    public void testAdminAccess() {
        SecurityContext.setSession(new UserSession(1L, "admin", UserRole.ADMIN, "machine"));

        // Admin should access EVERYTHING
        assertDoesNotThrow(() -> authzService.checkPermission(Feature.MASTERS));
        assertDoesNotThrow(() -> authzService.checkPermission(Feature.BILLING));
        assertDoesNotThrow(() -> authzService.checkPermission(Feature.REPORTS));
        assertDoesNotThrow(() -> authzService.checkPermission(Feature.SETTINGS));
    }

    @Test
    public void testStaffAccess() {
        SecurityContext.setSession(new UserSession(2L, "staff", UserRole.STAFF, "machine"));

        // Staff = BILLING only
        assertDoesNotThrow(() -> authzService.checkPermission(Feature.BILLING));

        assertThrows(AuthException.class, () -> authzService.checkPermission(Feature.MASTERS));
        assertThrows(AuthException.class, () -> authzService.checkPermission(Feature.REPORTS));
    }

    @Test
    public void testAccountantAccess() {
        SecurityContext.setSession(new UserSession(3L, "acc", UserRole.ACCOUNTANT, "machine"));

        // Accountant = REPORTS only
        assertDoesNotThrow(() -> authzService.checkPermission(Feature.REPORTS));

        assertThrows(AuthException.class, () -> authzService.checkPermission(Feature.BILLING));
        assertThrows(AuthException.class, () -> authzService.checkPermission(Feature.MASTERS));
    }

    @Test
    public void testNoSession() {
        SecurityContext.clear();
        assertThrows(AuthException.class, () -> authzService.checkPermission(Feature.BILLING));
    }
}
