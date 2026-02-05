package com.jewellery.erp.test.phase2.repository;

import com.jewellery.erp.config.AppConfig;
import com.jewellery.erp.master.entity.Metal;
import com.jewellery.erp.master.repository.MetalRepository;
import com.jewellery.erp.test.phase2.TestBase;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * REPO TEST 3: Deactivated Records Hidden
 */
public class SoftDeleteTest extends TestBase {

    private final MetalRepository repository = new MetalRepository();

    @org.junit.jupiter.api.BeforeEach
    public void setup() {
        cleanMasterTables();
    }

    @Test
    public void testDeactivatedRecordsNotReturnedByFindAllActive() {
        Transaction tx = null;
        try (Session session = AppConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Create metal
            Metal metal = new Metal();
            metal.setName("Gold");
            metal.setCode("AU");
            repository.save(session, metal);

            // Verify it appears in active list
            List<Metal> activeList1 = repository.findAllActive(session);
            assertEquals(1, activeList1.size(), "Active metal should be returned");

            // Deactivate
            repository.deactivate(session, metal.getId());

            // Clear session to ensure we read from DB, avoiding stale L1 cache
            session.clear();

            // Verify it no longer appears in active list
            List<Metal> activeList2 = repository.findAllActive(session);
            assertEquals(0, activeList2.size(), "Deactivated metal should not be returned");

            // But should still exist in findAll
            List<Metal> allList = repository.findAll(session);
            assertEquals(1, allList.size(), "Deactivated metal should still exist (soft delete)");
            assertFalse(allList.get(0).getActive(), "Metal should be marked inactive");

            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive())
                tx.rollback();
            fail("Soft delete test failed: " + e.getMessage());
        }
    }
}
