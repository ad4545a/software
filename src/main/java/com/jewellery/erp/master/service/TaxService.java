package com.jewellery.erp.master.service;

import com.jewellery.erp.config.AppConfig;
import com.jewellery.erp.master.entity.Tax;
import com.jewellery.erp.master.repository.TaxRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class TaxService {

    private final TaxRepository repository = new TaxRepository();

    public Tax createTax(Tax tax) {
        if (tax == null) {
            throw new IllegalArgumentException("Tax cannot be null");
        }

        Transaction tx = null;
        Session session = null;
        try {
            session = AppConfig.getSessionFactory().openSession();
            tx = session.beginTransaction();

            // Rule: Name must be unique
            if (repository.findByName(session, tax.getName()).isPresent()) {
                throw new IllegalArgumentException("Tax with name '" + tax.getName() + "' already exists");
            }

            // Rule: Percentage between 0-100
            if (tax.getPercentage() < 0 || tax.getPercentage() > 100) {
                throw new IllegalArgumentException("Percentage must be between 0 and 100");
            }

            // Rule: Only one default tax allowed
            if (tax.getIsDefault() && repository.countDefaultTaxes(session) > 0) {
                throw new IllegalStateException(
                        "Only one default tax is allowed. Please unset the existing default first.");
            }

            repository.save(session, tax);
            tx.commit();
            return tax;

        } catch (IllegalArgumentException | IllegalStateException e) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception ignore) {
                }
            }
            throw e;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception ignore) {
                }
            }
            throw new RuntimeException("Failed to create tax", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public Tax updateTax(Tax tax) {
        Transaction tx = null;
        Session session = null;
        try {
            session = AppConfig.getSessionFactory().openSession();
            tx = session.beginTransaction();

            // Rule: Percentage between 0-100
            if (tax.getPercentage() < 0 || tax.getPercentage() > 100) {
                throw new IllegalArgumentException("Percentage must be between 0 and 100");
            }

            repository.update(session, tax);
            tx.commit();
            return tax;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception ignore) {
                }
            }
            throw new RuntimeException("Failed to update tax", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void deactivateTax(Long id) {
        Transaction tx = null;
        Session session = null;
        try {
            session = AppConfig.getSessionFactory().openSession();
            tx = session.beginTransaction();
            repository.deactivate(session, id);
            tx.commit();
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception ignore) {
                }
            }
            throw new RuntimeException("Failed to deactivate tax", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public List<Tax> listAllActive() {
        try (Session session = AppConfig.getSessionFactory().openSession()) {
            return repository.findAllActive(session);
        }
    }

    public Tax getDefaultTax() {
        try (Session session = AppConfig.getSessionFactory().openSession()) {
            return repository.findDefault(session).orElse(null);
        }
    }
}
