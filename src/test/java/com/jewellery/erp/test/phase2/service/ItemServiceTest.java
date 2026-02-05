package com.jewellery.erp.test.phase2.service;

import com.jewellery.erp.master.entity.Item;
import com.jewellery.erp.master.entity.Metal;
import com.jewellery.erp.master.entity.Purity;
import com.jewellery.erp.master.service.ItemService;
import com.jewellery.erp.master.service.MetalService;
import com.jewellery.erp.master.service.PurityService;
import com.jewellery.erp.test.phase2.TestBase;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TEST I1: Item Purity Cross-Validation
 * TEST I2: Wastage Validation
 */
public class ItemServiceTest extends TestBase {

    private final MetalService metalService = new MetalService();
    private final PurityService purityService = new PurityService();
    private final ItemService itemService = new ItemService();

    @Test
    public void testPurityMustBelongToSelectedMetal() {
        Metal m1 = createMetal();
        Purity p1 = createPurity(m1);

        Metal m2 = createMetal(); // Different metal

        Item item = new Item();
        item.setName("Item-" + UUID.randomUUID());
        item.setMetalId(m2.getId()); // Item uses M2
        item.setPurityId(p1.getId()); // Purity belongs to M1 -> Mismatch!
        item.setWastagePercent(1.0);

        Exception ex = assertThrows(Exception.class, () -> {
            itemService.createItem(item);
        }, "Should reject Item if Purity belongs to a different Metal");

        assertTrue(ex.getMessage().contains("Purity does not belong"),
                "Error message should mention purity mismatch. Got: " + ex.getMessage());
    }

    @Test
    public void testWastageCannotBeNegative() {
        Metal metal = createMetal();

        Item item = new Item();
        item.setName("Item-" + UUID.randomUUID());
        item.setMetalId(metal.getId());
        item.setWastagePercent(-1.0); // Invalid

        Exception ex = assertThrows(Exception.class, () -> {
            itemService.createItem(item);
        }, "Should reject negative wastage");

        assertTrue(ex.getMessage().contains("Wastage percentage cannot be negative"),
                "Error message should mention wastage validation. Got: " + ex.getMessage());
    }

    @Test
    public void testValidItemCreation() {
        Metal metal = createMetal();
        Purity purity = createPurity(metal);

        Item item = new Item();
        item.setName("ValidItem-" + UUID.randomUUID());
        item.setMetalId(metal.getId());
        item.setPurityId(purity.getId());
        item.setWastagePercent(0.0); // Valid

        assertDoesNotThrow(() -> {
            itemService.createItem(item);
        }, "Should accept valid item");
    }

    @Test
    public void testUniqueness() {
        Metal metal = createMetal();
        Purity purity = createPurity(metal);
        String name = "UniqueItem";

        Item i1 = new Item();
        i1.setName(name);
        i1.setMetalId(metal.getId());
        i1.setPurityId(purity.getId());
        itemService.createItem(i1);

        Item i2 = new Item();
        i2.setName(name); // Duplicate name+metal+purity
        i2.setMetalId(metal.getId());
        i2.setPurityId(purity.getId());

        assertThrows(Exception.class, () -> {
            itemService.createItem(i2);
        }, "Should reject duplicate item");
    }

    private Metal createMetal() {
        Metal metal = new Metal();
        String uid = UUID.randomUUID().toString();
        metal.setName("Metal-" + uid);
        metal.setCode("M-" + uid.substring(0, 5));
        return metalService.createMetal(metal);
    }

    private Purity createPurity(Metal metal) {
        Purity purity = new Purity();
        purity.setMetalId(metal.getId());
        purity.setName("Purity-" + UUID.randomUUID());
        purity.setPercentage(90.0);
        return purityService.createPurity(purity);
    }
}
