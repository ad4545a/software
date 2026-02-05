package com.jewellery.erp.test.phase2.service;

import com.jewellery.erp.master.entity.Metal;
import com.jewellery.erp.master.entity.Purity;
import com.jewellery.erp.master.service.MetalService;
import com.jewellery.erp.master.service.PurityService;
import com.jewellery.erp.test.phase2.TestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TEST M1: Cannot Deactivate If Used
 */
public class MetalServiceTest extends TestBase {

    private final MetalService metalService = new MetalService();
    private final PurityService purityService = new PurityService();

    @Test
    public void testCannotDeactivateMetalIfUsedByPurity() {
        // Create metal
        Metal metal = new Metal();
        metal.setName("Gold");
        metal.setCode("AU");
        metal = metalService.createMetal(metal);

        // Create purity under this metal
        Purity purity = new Purity();
        purity.setMetalId(metal.getId());
        purity.setName("916");
        purity.setPercentage(91.6);
        purityService.createPurity(purity);

        // Attempt to deactivate metal
        Long metalId = metal.getId();
        Exception ex = assertThrows(Exception.class, () -> {
            metalService.deactivateMetal(metalId);
        }, "Should not allow deactivating metal that is referenced by purity");

        String message = ex.getMessage();
        assertTrue(message.toLowerCase().contains("metal") && message.toLowerCase().contains("purities"),
                "Error message should mention metal and purity relationship. Got: " + message);
    }

    @Test
    public void testCanDeactivateUnusedMetal() {
        Metal metal = new Metal();
        metal.setName("Platinum");
        metal.setCode("PT");
        metal = metalService.createMetal(metal);

        Long metalId = metal.getId();
        assertDoesNotThrow(() -> {
            metalService.deactivateMetal(metalId);
        }, "Should allow deactivating unused metal");
    }

    @Test
    public void testNameUniqueness() {
        Metal m1 = new Metal();
        m1.setName("Silver");
        m1.setCode("AG");
        metalService.createMetal(m1);

        Metal m2 = new Metal();
        m2.setName("Silver"); // Duplicate
        m2.setCode("AG2");

        assertThrows(Exception.class, () -> {
            metalService.createMetal(m2);
        }, "Should reject duplicate metal name");
    }

    @Test
    public void testCodeUniqueness() {
        Metal m1 = new Metal();
        m1.setName("Gold");
        m1.setCode("AU");
        metalService.createMetal(m1);

        Metal m2 = new Metal();
        m2.setName("Gold Alloy");
        m2.setCode("AU"); // Duplicate code

        assertThrows(Exception.class, () -> {
            metalService.createMetal(m2);
        }, "Should reject duplicate metal code");
    }
}
