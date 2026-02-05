package com.jewellery.erp.test.phase2.service;

import com.jewellery.erp.master.entity.Tax;
import com.jewellery.erp.master.service.TaxService;
import com.jewellery.erp.test.phase2.TestBase;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TEST T1: Only One Default Tax
 */
public class TaxServiceTest extends TestBase {

    private final TaxService taxService = new TaxService();

    @Test
    public void testOnlyOneDefaultTaxAllowed() {
        // Create first default tax
        Tax t1 = new Tax();
        t1.setName("GST 3%");
        t1.setPercentage(3.0);
        t1.setIsDefault(true);
        taxService.createTax(t1);

        // Attempt to create second default tax
        Tax t2 = new Tax();
        t2.setName("GST 5%");
        t2.setPercentage(5.0);
        t2.setIsDefault(true); // Conflict!

        Exception ex = assertThrows(Exception.class, () -> {
            taxService.createTax(t2);
        }, "Should not allow multiple default taxes");

        assertTrue(ex.getMessage().contains("Only one default tax is allowed"),
                "Error message should mention default tax conflict. Got: " + ex.getMessage());
    }

    @Test
    public void testCanCreateNonDefaultTaxes() {
        Tax t1 = new Tax();
        t1.setName("GST 3%");
        t1.setPercentage(3.0);
        t1.setIsDefault(false);
        taxService.createTax(t1);

        Tax t2 = new Tax();
        t2.setName("GST 5%");
        t2.setPercentage(5.0);
        t2.setIsDefault(false);

        assertDoesNotThrow(() -> {
            taxService.createTax(t2);
        }, "Should allow multiple non-default taxes");
    }

    @Test
    public void testPercentageValidation() {
        Tax t1 = new Tax();
        t1.setName("Invalid");
        t1.setPercentage(-5.0); // Invalid

        assertThrows(Exception.class, () -> {
            taxService.createTax(t1);
        }, "Should reject negative percentage");

        Tax t2 = new Tax();
        t2.setName("Invalid2");
        t2.setPercentage(150.0); // Invalid

        assertThrows(Exception.class, () -> {
            taxService.createTax(t2);
        }, "Should reject percentage > 100");
    }

    @Test
    public void testGetDefaultTax() {
        Tax tax = new Tax();
        tax.setName("GST 3%");
        tax.setPercentage(3.0);
        tax.setIsDefault(true);
        taxService.createTax(tax);

        Tax defaultTax = taxService.getDefaultTax();
        assertNotNull(defaultTax, "Should retrieve default tax");
        assertEquals("GST 3%", defaultTax.getName());
    }
}
