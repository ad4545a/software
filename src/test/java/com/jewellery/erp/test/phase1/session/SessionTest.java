package com.jewellery.erp.test.phase1.session;

import com.jewellery.erp.model.UserRole;
import com.jewellery.erp.security.UserSession;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import static org.junit.jupiter.api.Assertions.*;

public class SessionTest {

    @Test
    public void testSessionCreation() {
        UserSession session = new UserSession(1L, "test", UserRole.STAFF, "machine-id");
        assertTrue(session.isValid(), "New session should be valid");
        assertEquals("test", session.getUsername());
        assertNotNull(session.getLoginTime());
    }

    @Test
    public void testTimeout() throws Exception {
        UserSession session = new UserSession(1L, "test", UserRole.STAFF, "machine-id");

        // Reflection to modify lastActivityMillis to > 30 mins ago
        Field lastActivityField = UserSession.class.getDeclaredField("lastActivityMillis");
        lastActivityField.setAccessible(true);
        long oldTimestamp = System.currentTimeMillis() - (31 * 60 * 1000); // 31 minutes ago

        // Access the AtomicLong and set it
        java.util.concurrent.atomic.AtomicLong atomicField = (java.util.concurrent.atomic.AtomicLong) lastActivityField
                .get(session);
        atomicField.set(oldTimestamp);

        assertFalse(session.isValid(), "Session should be invalid after 30+ minutes");
    }

    @Test
    public void testActivityRefresh() throws Exception {
        UserSession session = new UserSession(1L, "test", UserRole.STAFF, "machine-id");

        // Set to 29 minutes ago
        Field lastActivityField = UserSession.class.getDeclaredField("lastActivityMillis");
        lastActivityField.setAccessible(true);
        long oldTimestamp = System.currentTimeMillis() - (29 * 60 * 1000);

        java.util.concurrent.atomic.AtomicLong atomicField = (java.util.concurrent.atomic.AtomicLong) lastActivityField
                .get(session);
        atomicField.set(oldTimestamp);

        assertTrue(session.isValid(), "Should still be valid at 29 mins");

        // Touch to refresh
        session.touch();

        // Should now be valid again
        assertTrue(session.isValid(), "Should be valid after touch()");
    }
}
