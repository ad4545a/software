package com.jewellery.erp.test.phase2.repository;

import com.jewellery.erp.config.AppConfig;
import com.jewellery.erp.master.entity.*;
import com.jewellery.erp.test.phase2.TestBase;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * REPO TEST 1: UNIQUE Constraints
 * Tests that database enforces uniqueness rules
 */
public class UniqueConstraintTest extends TestBase {

    @Test
    public void testCompanyNameUnique() {
        Company c1 = new Company();
        c1.setName("ABC Jewellers");
        c1.setAddress("Street 1");

        Company c2 = new Company();
        c2.setName("ABC Jewellers"); // Duplicate name
        c2.setAddress("Street 2");

        assertThrows(Exception.class, () -> {
            persistBoth(c1, c2);
        }, "Duplicate company name should fail");
    }

    @Test
    public void testMetalNameUnique() {
        Metal m1 = new Metal();
        m1.setName("Gold");
        m1.setCode("AU");

        Metal m2 = new Metal();
        m2.setName("Gold"); // Duplicate name
        m2.setCode("AU2");

        assertThrows(Exception.class, () -> {
            persistBoth(m1, m2);
        }, "Duplicate metal name should fail");
    }

    @Test
    public void testMetalCodeUnique() {
        Metal m1 = new Metal();
        m1.setName("Gold");
        m1.setCode("AU");

        Metal m2 = new Metal();
        m2.setName("Silver");
        m2.setCode("AU"); // Duplicate code

        assertThrows(Exception.class, () -> {
            persistBoth(m1, m2);
        }, "Duplicate metal code should fail");
    }

    @Test
    public void testPurityMetalNameUnique() {
        // Create metal first
        Metal metal = new Metal();
        metal.setName("Gold");
        metal.setCode("AU");
        metal = persist(metal);

        Purity p1 = new Purity();
        p1.setMetalId(metal.getId());
        p1.setName("916");
        p1.setPercentage(91.6);

        Purity p2 = new Purity();
        p2.setMetalId(metal.getId());
        p2.setName("916"); // Duplicate metal+name
        p2.setPercentage(92.0);

        assertThrows(Exception.class, () -> {
            persistBoth(p1, p2);
        }, "Duplicate purity (metal+name) should fail");
    }

    @Test
    public void testStoneNameUnique() {
        Stone s1 = new Stone();
        s1.setName("Diamond");
        s1.setUnit("Ct");
        s1.setRateType("PerCt");

        Stone s2 = new Stone();
        s2.setName("Diamond"); // Duplicate
        s2.setUnit("Gm");
        s2.setRateType("PerGm");

        assertThrows(Exception.class, () -> {
            persistBoth(s1, s2);
        }, "Duplicate stone name should fail");
    }

    @Test
    public void testTaxNameUnique() {
        Tax t1 = new Tax();
        t1.setName("GST 3%");
        t1.setPercentage(3.0);

        Tax t2 = new Tax();
        t2.setName("GST 3%"); // Duplicate
        t2.setPercentage(5.0);

        assertThrows(Exception.class, () -> {
            persistBoth(t1, t2);
        }, "Duplicate tax name should fail");
    }

    @Test
    public void testCounterNameUnique() {
        Counter c1 = new Counter();
        c1.setName("Counter 1");
        c1.setLocation("Main Hall");

        Counter c2 = new Counter();
        c2.setName("Counter 1"); // Duplicate
        c2.setLocation("Side Hall");

        assertThrows(Exception.class, () -> {
            persistBoth(c1, c2);
        }, "Duplicate counter name should fail");
    }

    // Helpers
    private <T> T persist(T entity) {
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

    private <T> void persistBoth(T entity1, T entity2) {
        Transaction tx = null;
        try (Session session = AppConfig.getSessionFactory().openSession()) {
            tx = session.beginTransaction();
            session.persist(entity1);
            session.flush();
            session.persist(entity2);
            session.flush();
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive())
                tx.rollback();
            throw e;
        }
    }
}
