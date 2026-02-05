package com.jewellery.erp.test.phase2.service;

import com.jewellery.erp.master.entity.Stone;
import com.jewellery.erp.master.service.StoneService;
import com.jewellery.erp.test.phase2.TestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TEST S1: Mandatory Unit
 */
public class StoneServiceTest extends TestBase {

    private final StoneService stoneService = new StoneService();

    @Test
    public void testUnitIsMandatory() {
        Stone stone = new Stone();
        stone.setName("Diamond");
        stone.setUnit(null); // Missing unit
        stone.setRateType("PerCt");

        Exception ex = assertThrows(Exception.class, () -> {
            stoneService.createStone(stone);
        }, "Should reject stone without unit");

        assertTrue(ex.getMessage().contains("Unit is mandatory"),
                "Error message should mention unit is mandatory. Got: " + ex.getMessage());
    }

    @Test
    public void testEmptyUnitRejected() {
        Stone stone = new Stone();
        stone.setName("Ruby");
        stone.setUnit(""); // Empty unit
        stone.setRateType("PerGm");

        Exception ex = assertThrows(Exception.class, () -> {
            stoneService.createStone(stone);
        }, "Should reject stone with empty unit");

        assertTrue(ex.getMessage().contains("Unit"),
                "Error message should mention unit");
    }

    @Test
    public void testValidStoneCreation() {
        Stone stone = new Stone();
        stone.setName("Emerald");
        stone.setUnit("Ct");
        stone.setRateType("PerCt");

        assertDoesNotThrow(() -> {
            stoneService.createStone(stone);
        }, "Valid stone should be created successfully");
    }

    @Test
    public void testNameUniqueness() {
        Stone s1 = new Stone();
        s1.setName("Sapphire");
        s1.setUnit("Ct");
        s1.setRateType("PerCt");
        stoneService.createStone(s1);

        Stone s2 = new Stone();
        s2.setName("Sapphire"); // Duplicate
        s2.setUnit("Gm");
        s2.setRateType("PerGm");

        assertThrows(Exception.class, () -> {
            stoneService.createStone(s2);
        }, "Should reject duplicate stone name");
    }
}
