package com.jewellery.erp.master.service;

import com.jewellery.erp.config.AppConfig;
import com.jewellery.erp.master.entity.Purity;
import com.jewellery.erp.master.repository.PurityRepository;
import com.jewellery.erp.master.repository.MetalRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class PurityService {

    private final PurityRepository repository = new PurityRepository();
    private final MetalRepository metalRepository = new MetalRepository();

    public Purity createPurity(Purity purity) {
        if (purity == null) {
            throw new IllegalArgumentException("Purity cannot be null");
        }
        if (purity.getName() == null || purity.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Purity name is required");
        }

        Transaction tx = null;
        Session session = null;
        try {
            session = AppConfig.getSessionFactory().openSession();
            tx = session.beginTransaction();

            // Rule: Validate percentage not null and between 0-100
            if (purity.getPercentage() == null) {
                throw new IllegalArgumentException("Percentage is required");
            }
            if (purity.getPercentage() < 0 || purity.getPercentage() > 100) {
                throw new IllegalArgumentException("Percentage must be between 0 and 100");
            }

            // Rule: Purity must belong to existing Metal (explicit check)
            if (purity.getMetalId() == null) {
                throw new IllegalArgumentException("Metal is required for Purity");
            }

            // Check metal existence
            boolean metalExists = metalRepository.findById(session, purity.getMetalId()).isPresent();
            if (!metalExists) {
                throw new IllegalArgumentException("Metal does not exist");
            }

            // Rule: Unique (metalId + name)
            if (repository.findByMetalAndName(session, purity.getMetalId(), purity.getName()).isPresent()) {
                throw new IllegalArgumentException("Purity '" + purity.getName() + "' already exists for this metal");
            }

            repository.save(session, purity);
            tx.commit();
            return purity;

        } catch (IllegalArgumentException e) {
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
            throw new RuntimeException("Failed to create purity", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public Purity updatePurity(Purity purity) {
        if (purity == null) {
            throw new IllegalArgumentException("Purity cannot be null");
        }
        if (purity.getId() == null) {
            throw new IllegalArgumentException("Purity ID is required for update");
        }
        Transaction tx = null;
        Session session = null;
        try {
            session = AppConfig.getSessionFactory().openSession();
            tx = session.beginTransaction();

            // Rule: Percentage between 0-100
            if (purity.getPercentage() < 0 || purity.getPercentage() > 100) {
                throw new IllegalArgumentException("Percentage must be between 0 and 100");
            }

            repository.update(session, purity);
            tx.commit();
            return purity;

        } catch (IllegalArgumentException e) {
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
            throw new RuntimeException("Failed to update purity", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void deactivatePurity(Long id) {
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
            throw new RuntimeException("Failed to deactivate purity", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public List<Purity> listAllActive() {
        try (Session session = AppConfig.getSessionFactory().openSession()) {
            return repository.findAllActive(session);
        }
    }

    public List<Purity> listByMetal(Long metalId) {
        try (Session session = AppConfig.getSessionFactory().openSession()) {
            return repository.findByMetal(session, metalId);
        }
    }
}
