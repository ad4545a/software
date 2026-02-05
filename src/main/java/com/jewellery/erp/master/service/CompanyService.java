package com.jewellery.erp.master.service;

import com.jewellery.erp.config.AppConfig;
import com.jewellery.erp.master.entity.Company;
import com.jewellery.erp.master.repository.CompanyRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;
import java.util.Optional;

public class CompanyService {

    private final CompanyRepository repository = new CompanyRepository();

    public Company createCompany(Company company) {
        if (company == null) {
            throw new IllegalArgumentException("Company cannot be null");
        }
        if (company.getName() == null || company.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Company name is required");
        }

        Transaction tx = null;
        Session session = null;
        try {
            session = AppConfig.getSessionFactory().openSession();
            tx = session.beginTransaction();

            // Rule: Name must be unique
            if (repository.findByName(session, company.getName()).isPresent()) {
                throw new IllegalArgumentException("Company with name '" + company.getName() + "' already exists");
            }

            // Rule: Validate GST format (basic check, offline)
            if (company.getGstNumber() != null && !company.getGstNumber().isEmpty()) {
                if (!isValidGstFormat(company.getGstNumber())) {
                    throw new IllegalArgumentException("Invalid GST number format");
                }
            }

            // Rule: Only one active company allowed
            if (company.getActive()) {
                List<Company> active = repository.findAllActive(session);
                if (!active.isEmpty()) {
                    throw new IllegalStateException(
                            "Only one active company allowed. Please deactivate the existing company first.");
                }
            }

            repository.save(session, company);
            tx.commit();
            return company;

        } catch (IllegalArgumentException | IllegalStateException e) {
            // ✅ Validation error → bubble up
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception ignore) {
                }
            }
            throw e;
        } catch (Exception e) {
            // ❌ System/DB error → wrap
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception ignore) {
                }
            }
            throw new RuntimeException("Failed to create company", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public Company updateCompany(Company company) {
        if (company == null) {
            throw new IllegalArgumentException("Company cannot be null");
        }
        if (company.getId() == null) {
            throw new IllegalArgumentException("Company ID is required for update");
        }
        Transaction tx = null;
        Session session = null;
        try {
            session = AppConfig.getSessionFactory().openSession();
            tx = session.beginTransaction();

            // Rule: Only one active company allowed
            if (company.getActive()) {
                List<Company> activeList = repository.findAllActive(session);
                for (Company c : activeList) {
                    if (!c.getId().equals(company.getId())) {
                        throw new IllegalStateException(
                                "Only one active company allowed. Please deactivate the existing company first.");
                    }
                }
            }

            repository.update(session, company);
            tx.commit();
            return company;

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
            throw new RuntimeException("Failed to update company", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void deactivateCompany(Long id) {
        Transaction tx = null;
        Session session = null;
        try {
            session = AppConfig.getSessionFactory().openSession();
            tx = session.beginTransaction();

            // Rule: Cannot deactivate last active company
            List<Company> activeList = repository.findAllActive(session);
            if (activeList.size() <= 1) {
                Company c = session.get(Company.class, id);
                if (c != null && c.getActive()) {
                    if (activeList.size() == 1 && activeList.get(0).getId().equals(id)) {
                        throw new IllegalStateException("Cannot deactivate the only active company");
                    }
                }
            }

            repository.deactivate(session, id);
            tx.commit();

        } catch (IllegalStateException e) {
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
            throw new RuntimeException("Failed to deactivate company", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public Company getActiveCompany() {
        try (Session session = AppConfig.getSessionFactory().openSession()) {
            return repository.findActiveCompany(session).orElse(null);
        }
    }

    // Basic GST Validation (15 chars, alphanumeric)
    // Format: 22AAAAA0000A1Z5
    private boolean isValidGstFormat(String gst) {
        // Updated Regex: Matches standard Indian GST format
        return gst.matches("^[0-9]{2}[A-Z]{5}[0-9]{4}[A-Z]{1}[1-9A-Z]{1}Z[0-9A-Z]{1}$");
    }
}
