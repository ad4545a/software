package com.jewellery.erp.test.phase2.service;

import com.jewellery.erp.master.entity.Metal;
import com.jewellery.erp.master.entity.Purity;
import com.jewellery.erp.master.service.MetalService;
import com.jewellery.erp.master.service.PurityService;
import com.jewellery.erp.test.phase2.TestBase;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TEST P1: Purity Must Belong to Metal
 * TEST P2: Percentage Validation
 */
public class PurityServiceTest extends TestBase {

    private final MetalService metalService = new MetalService();
    private final PurityService purityService = new PurityService();

    @Test
    public void testPurityMustBelongToExistingMetal() {
        Purity purity = new Purity();
        purity.setMetalId(99999L); // Non-existent metal
        purity.setName("916");
        purity.setPercentage(91.6);

        Exception ex = assertThrows(Exception.class, () -> {
            purityService.createPurity(purity);
        }, "Should reject purity with non-existent metal");

        assertTrue(ex.getMessage().contains("Metal does not exist") || ex.getMessage().contains("Metal is required"),
                "Error message should mention missing metal. Got: " + ex.getMessage());
    }

    @Test
    public void testPercentageCannotBeNegative() {
        Metal metal = createMetal();

        Purity purity = new Purity();
        purity.setMetalId(metal.getId());
        purity.setName("Invalid-" + UUID.randomUUID());
        purity.setPercentage(-10.0); // Invalid

        Exception ex = assertThrows(Exception.class, () -> {
            purityService.createPurity(purity);
        }, "Should reject negative percentage");

        assertTrue((ex.getMessage().contains("Percentage") && ex.getMessage().contains("between 0 and 100")),
                "Error message should mention percentage range. Got: " + ex.getMessage());
    }

    @Test
    public void testPercentageCannotExceed100() {
        Metal metal = createMetal();

        Purity purity = new Purity();
        purity.setMetalId(metal.getId());
        purity.setName("Invalid-" + UUID.randomUUID());
        purity.setPercentage(150.0); // Invalid

        Exception ex = assertThrows(Exception.class, () -> {
            purityService.createPurity(purity);
        }, "Should reject percentage > 100");

        assertTrue((ex.getMessage().contains("Percentage") && ex.getMessage().contains("between 0 and 100")),
                "Error message should mention percentage range. Got: " + ex.getMessage());
    }

    @Test
    public void testValidPercentageAccepted() {
        Metal metal = createMetal();

        Purity purity = new Purity();
        purity.setMetalId(metal.getId());
        purity.setName("916-" + UUID.randomUUID());
        purity.setPercentage(91.6); // Valid

        assertDoesNotThrow(() -> {
            purityService.createPurity(purity);
        }, "Valid percentage should be accepted");
    }

    @Test
    public void testUniquenessWithinMetal() {
        Metal metal = createMetal();

        Purity p1 = new Purity();
        p1.setMetalId(metal.getId());
        p1.setName("Unique916");
        p1.setPercentage(91.6);
        purityService.createPurity(p1);

        Purity p2 = new Purity();
        p2.setMetalId(metal.getId());
        p2.setName("Unique916"); // Duplicate within same metal
        p2.setPercentage(92.0);

        assertThrows(Exception.class, () -> {
            purityService.createPurity(p2);
        }, "Should reject duplicate purity name within same metal");
    }

    private Metal createMetal() {
        Metal metal = new Metal();
        String uid = UUID.randomUUID().toString();
        metal.setName("Gold-" + uid);
        metal.setCode("AU-" + uid.substring(0, 5));
        return metalService.createMetal(metal);
    }
}
