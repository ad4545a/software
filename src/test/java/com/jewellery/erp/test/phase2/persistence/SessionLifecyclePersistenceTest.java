package com.jewellery.erp.test.phase2.persistence;

import com.jewellery.erp.config.AppConfig;
import com.jewellery.erp.master.entity.*;
import com.jewellery.erp.master.service.*;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * TEST R1: Session Lifecycle Persistence
 * Verified with clean restart logic.
 */

public class SessionLifecyclePersistenceTest {

    @org.junit.jupiter.api.AfterEach
    void cleanup() {
        // ❌ DO NOT clean DB here
    }

    @Test
    public void testDataSurvivesRestart() {
        // Ensure standard initialization
        AppConfig.initialize();

        // Phase 1: Create data
        CompanyService companyService = new CompanyService();
        MetalService metalService = new MetalService();
        TaxService taxService = new TaxService();

        // Create if not exists (to handle re-runs or shared DB state)
        if (companyService.getActiveCompany() == null) {
            Company company = new Company();
            company.setName("Test Company R-" + System.currentTimeMillis());
            company.setAddress("Address");
            company.setActive(true);
            try {
                companyService.createCompany(company);
            } catch (Exception ignored) {
                // Ignore single company constraint if race
            }
        }

        Metal metal = new Metal();
        metal.setName("Gold-R-" + System.currentTimeMillis());
        metal.setCode("AU-" + (System.currentTimeMillis() % 10000));
        metalService.createMetal(metal);

        // Phase 2: Simulate restart (close and reopen SessionFactory)
        SessionFactory oldFactory = AppConfig.getSessionFactory();
        if (oldFactory != null && !oldFactory.isClosed()) {
            oldFactory.close();
        }

        AppConfig.initialize(); // Reinitialize logic

        // Phase 3: Verify data persisted
        MetalService newMetalService = new MetalService();
        List<Metal> metals = newMetalService.listAllActive();

        // We look for the metal we created
        boolean found = false;
        for (Metal m : metals) {
            if (m.getName().equals(metal.getName())) {
                found = true;
                break;
            }
        }
        assertTrue(found, "Metal should survive restart");
    }
}
