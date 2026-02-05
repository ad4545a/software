package com.jewellery.erp.test.phase2.service;

import com.jewellery.erp.master.entity.Company;
import com.jewellery.erp.master.service.CompanyService;
import com.jewellery.erp.test.phase2.TestBase;
import org.junit.jupiter.api.Test;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TEST C1: Single Active Company
 * TEST C2: GST Validation
 */
public class CompanyServiceTest extends TestBase {

    private final CompanyService companyService = new CompanyService();

    @Test
    public void testCannotCreateMultipleActiveCompanies() {
        // Create first active company
        Company c1 = new Company();
        c1.setName("Company A " + UUID.randomUUID());
        c1.setAddress("Addr A");
        c1.setActive(true);
        companyService.createCompany(c1);

        // Try to create second active company
        Company c2 = new Company();
        c2.setName("Company B " + UUID.randomUUID());
        c2.setAddress("Addr B");
        c2.setActive(true);

        Exception ex = assertThrows(Exception.class, () -> {
            companyService.createCompany(c2);
        }, "Should reject second active company");

        assertTrue(ex.getMessage().contains("Only one active company allowed"),
                "Error message should mention active restriction. Got: " + ex.getMessage());
    }

    @Test
    public void testGSTValidation() {
        Company company = new Company();
        company.setName("Invalid GST " + UUID.randomUUID());
        company.setGstNumber("INVALID123"); // Invalid format

        Exception ex = assertThrows(Exception.class, () -> {
            companyService.createCompany(company);
        }, "Should reject invalid GST");

        assertTrue(ex.getMessage().contains("Invalid GST number format"),
                "Error message should mention GST validity. Got: " + ex.getMessage());
    }

    @Test
    public void testValidGSTAccepted() {
        Company company = new Company();
        company.setName("Valid GST " + UUID.randomUUID());
        // Valid Format: 22AAAAA0000A1Z5
        company.setGstNumber("22AAAAA0000A1Z5");

        assertDoesNotThrow(() -> {
            companyService.createCompany(company);
        }, "Should accept valid GST number");
    }

    @Test
    public void testCannotDeactivateLastCompany() {
        // Create one active company
        Company company = new Company();
        company.setName("Last Company " + UUID.randomUUID());
        company.setActive(true);
        company = companyService.createCompany(company);
        Long id = company.getId();

        // Try to deactivate
        Exception ex = assertThrows(Exception.class, () -> {
            companyService.deactivateCompany(id);
        }, "Should not allow deactivating the only active company");

        assertTrue(ex.getMessage().contains("only active company"),
                "Error message should mention restriction");
    }

    @Test
    public void testGetActiveCompany() {
        Company company = new Company();
        String name = "Active Co " + UUID.randomUUID();
        company.setName(name);
        company.setActive(true);
        companyService.createCompany(company);

        Company retrieved = companyService.getActiveCompany();
        assertNotNull(retrieved);
        assertEquals(name, retrieved.getName());
    }
}
