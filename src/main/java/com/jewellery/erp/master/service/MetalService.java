package com.jewellery.erp.master.service;

import com.jewellery.erp.config.AppConfig;
import com.jewellery.erp.master.entity.Metal;
import com.jewellery.erp.master.repository.MetalRepository;
import com.jewellery.erp.master.repository.PurityRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class MetalService {

    private final MetalRepository repository = new MetalRepository();
    private final PurityRepository purityRepository = new PurityRepository();

    public Metal createMetal(Metal metal) {
        if (metal == null) {
            throw new IllegalArgumentException("Metal cannot be null");
        }
        if (metal.getName() == null || metal.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Metal name is required");
        }

        Transaction tx = null;
        Session session = null;
        try {
            session = AppConfig.getSessionFactory().openSession();
            tx = session.beginTransaction();

            // Rule: Name must be unique
            if (repository.findByName(session, metal.getName()).isPresent()) {
                throw new IllegalArgumentException("Metal with name '" + metal.getName() + "' already exists");
            }

            // Rule: Code must be unique
            if (repository.findByCode(session, metal.getCode()).isPresent()) {
                throw new IllegalArgumentException("Metal with code '" + metal.getCode() + "' already exists");
            }

            repository.save(session, metal);
            tx.commit();
            return metal;

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
            throw new RuntimeException("Failed to create metal", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public Metal updateMetal(Metal metal) {
        if (metal == null) {
            throw new IllegalArgumentException("Metal cannot be null");
        }
        if (metal.getId() == null) {
            throw new IllegalArgumentException("Metal ID is required for update");
        }
        Transaction tx = null;
        Session session = null;
        try {
            session = AppConfig.getSessionFactory().openSession();
            tx = session.beginTransaction();
            repository.update(session, metal);
            tx.commit();
            return metal;
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
            throw new RuntimeException("Failed to update metal", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void deactivateMetal(Long id) {
        Transaction tx = null;
        Session session = null;
        try {
            session = AppConfig.getSessionFactory().openSession();
            tx = session.beginTransaction();

            // Rule: Cannot deactivate if used by Purity
            long purityCount = purityRepository.countByMetal(session, id);
            if (purityCount > 0) {
                throw new IllegalStateException("Cannot deactivate metal that is used by " + purityCount + " purities");
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
            throw new RuntimeException("Failed to deactivate metal", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public List<Metal> listAllActive() {
        try (Session session = AppConfig.getSessionFactory().openSession()) {
            return repository.findAllActive(session);
        }
    }
}
