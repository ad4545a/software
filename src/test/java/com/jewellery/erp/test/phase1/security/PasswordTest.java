package com.jewellery.erp.test.phase1.security;

import com.jewellery.erp.util.PasswordUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PasswordTest {

    @Test
    public void testHashingCorrectness() {
        String raw = "SecureP@ss123";
        String hash = PasswordUtil.hashPassword(raw);

        assertNotNull(hash);
        assertNotEquals(raw, hash);
        assertTrue(hash.startsWith("$2a$12$"), "Should use BCrypt Cost 12");

        // Verify Correct Password
        assertTrue(PasswordUtil.checkPassword(raw, hash));
    }

    @Test
    public void testWrongPassword() {
        String raw = "password";
        String hash = PasswordUtil.hashPassword(raw);

        assertFalse(PasswordUtil.checkPassword("wrong", hash));
        assertFalse(PasswordUtil.checkPassword("", hash));
    }

    @Test
    public void testUniqueSalts() {
        String raw = "samepassword";
        String hash1 = PasswordUtil.hashPassword(raw);
        String hash2 = PasswordUtil.hashPassword(raw);

        assertNotEquals(hash1, hash2, "Same password hashed twice should have different salts");
    }

    @Test
    public void testNullInputs() {
        assertThrows(IllegalArgumentException.class, () -> PasswordUtil.hashPassword(null));
        assertFalse(PasswordUtil.checkPassword(null, "somehash"));
        assertFalse(PasswordUtil.checkPassword("pass", null));
    }
}
