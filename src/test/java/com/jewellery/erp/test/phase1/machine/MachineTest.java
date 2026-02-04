package com.jewellery.erp.test.phase1.machine;

import com.jewellery.erp.util.MachineUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MachineTest {

    @Test
    public void testDeterministicId() {
        String id1 = MachineUtil.getMachineId();
        String id2 = MachineUtil.getMachineId();

        assertNotNull(id1, "ID should not be null");
        assertFalse(id1.isEmpty(), "ID should not be empty");
        assertEquals(id1, id2, "Machine ID must be deterministic (same input -> same output)");
    }

    @Test
    public void testFormat() {
        String id = MachineUtil.getMachineId();
        // Check Hex format (only 0-9, a-f)
        assertTrue(id.matches("^[0-9a-fA-F]+$"), "ID should be Hexadecimal");
        // Check Length (SHA-256 is 64 hex chars)
        assertEquals(64, id.length(), "ID should be 64 characters long (SHA-256)");
    }
}
