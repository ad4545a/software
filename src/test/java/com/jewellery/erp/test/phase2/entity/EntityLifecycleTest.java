package com.jewellery.erp.test.phase2.entity;

import com.jewellery.erp.config.AppConfig;
import com.jewellery.erp.master.entity.*;
import com.jewellery.erp.test.phase2.TestBase;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ENTITY TEST 1: Audit Fields (@PrePersist, @PreUpdate)
 * ENTITY TEST 2: Active Flag Default
 */
public class EntityLifecycleTest extends TestBase {

    @Test
    public void testCompanyAuditFields() {
        testAuditFieldsForEntity(createCompany());
    }

    @Test
    public void testMetalAuditFields() {
        testAuditFieldsForEntity(createMetal());
    }

    @Test
    public void testPurityAuditFields() {
        // Need metal first
        Metal metal = createAndPersist(createMetal());
        Purity purity = new Purity();
        purity.setMetalId(metal.getId());
        purity.setName("916");
        purity.setPercentage(91.6);
        testAuditFieldsForEntity(purity);
    }

    @Test
    public void testStoneAuditFields() {
        testAuditFieldsForEntity(createStone());
    }

    @Test
    public void testTaxAuditFields() {
        testAuditFieldsForEntity(createTax());
    }

    @Test
    public void testCounterAuditFields() {
        testAuditFieldsForEntity(createCounter());
    }

    @Test
    public void testActiveFlagDefaultsToTrue() {
        Company company = createCompany();

        Transaction tx = null;
        try (Session session = AppConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(company);
            tx.commit();

            assertTrue(company.getActive(), "Active flag should default to true");
        } catch (Exception e) {
            if (tx != null && tx.isActive())
                tx.rollback();
            fail("Failed to test active flag: " + e.getMessage());
        }
    }

    // Helper method to test audit fields for any entity
    private <T extends BaseMasterEntity> void testAuditFieldsForEntity(T entity) {
        Transaction tx = null;
        try (Session session = AppConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();

            // Persist entity
            session.persist(entity);
            session.flush();

            // Verify createdAt and updatedAt are set
            assertNotNull(entity.getCreatedAt(), "createdAt should be set on @PrePersist");
            assertNotNull(entity.getUpdatedAt(), "updatedAt should be set on @PrePersist");

            var originalUpdatedAt = entity.getUpdatedAt();

            // Sleep to ensure timestamp changes
            Thread.sleep(10);

            // Update entity
            entity.setActive(false);
            session.merge(entity);
            session.flush();

            // Verify updatedAt changed
            assertNotEquals(originalUpdatedAt, entity.getUpdatedAt(),
                    "updatedAt should change on @PreUpdate");

            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive())
                tx.rollback();
            fail("Audit field test failed: " + e.getMessage());
        }
    }

    private <T extends BaseMasterEntity> T createAndPersist(T entity) {
        Transaction tx = null;
        try (Session session = AppConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(entity);
            tx.commit();
            return entity;
        } catch (Exception e) {
            if (tx != null && tx.isActive())
                tx.rollback();
            throw new RuntimeException(e);
        }
    }

    // Factory methods
    private Company createCompany() {
        Company c = new Company();
        c.setName("Test Company " + System.currentTimeMillis());
        c.setAddress("Test Address");
        return c;
    }

    private Metal createMetal() {
        Metal m = new Metal();
        m.setName("Gold-" + System.currentTimeMillis());
        m.setCode("AU-" + (System.currentTimeMillis() % 10000));
        return m;
    }

    private Stone createStone() {
        Stone s = new Stone();
        s.setName("Diamond-" + System.currentTimeMillis());
        s.setUnit("Ct");
        s.setRateType("PerCt");
        return s;
    }

    private Tax createTax() {
        Tax t = new Tax();
        t.setName("GST-" + System.currentTimeMillis());
        t.setPercentage(3.0);
        return t;
    }

    private Counter createCounter() {
        Counter c = new Counter();
        c.setName("Counter-" + System.currentTimeMillis());
        c.setLocation("Main Hall");
        return c;
    }
}
