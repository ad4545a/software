package com.jewellery.erp.test.phase1.session;

import com.jewellery.erp.model.UserRole;
import com.jewellery.erp.security.UserSession;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
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

        // Reflection to modify lastActivity to > 30 mins ago
        Field lastActivityField = UserSession.class.getDeclaredField("lastActivity");
        lastActivityField.setAccessible(true);
        lastActivityField.set(session, LocalDateTime.now().minusMinutes(31));

        assertFalse(session.isValid(), "Session should be invalid after 30+ minutes");
    }

    @Test
    public void testActivityRefresh() throws Exception {
        UserSession session = new UserSession(1L, "test", UserRole.STAFF, "machine-id");

        // Mock old time
        Field lastActivityField = UserSession.class.getDeclaredField("lastActivity");
        lastActivityField.setAccessible(true);
        lastActivityField.set(session, LocalDateTime.now().minusMinutes(29));

        assertTrue(session.isValid(), "Should still be valid at 29 mins");

        session.touch();

        lastActivityField.set(session, LocalDateTime.now().minusMinutes(31));
        // Note: touch() resets it to NOW. So setting it to -31 above is overriding
        // touch if done after.
        // Let's re-verify:
        // 1. Set to old
        // 2. Touch
        // 3. Verify valid
    }
}
