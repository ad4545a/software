package com.jewellery.erp.master.service;

import com.jewellery.erp.config.AppConfig;
import com.jewellery.erp.master.entity.Stone;
import com.jewellery.erp.master.repository.StoneRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class StoneService {

    private final StoneRepository repository = new StoneRepository();

    public Stone createStone(Stone stone) {
        if (stone == null) {
            throw new IllegalArgumentException("Stone cannot be null");
        }

        Transaction tx = null;
        Session session = null;
        try {
            session = AppConfig.getSessionFactory().openSession();
            tx = session.beginTransaction();

            // Rule: Name must be unique
            if (repository.findByName(session, stone.getName()).isPresent()) {
                throw new IllegalArgumentException("Stone with name '" + stone.getName() + "' already exists");
            }

            // Rule: Unit is mandatory
            if (stone.getUnit() == null || stone.getUnit().isEmpty()) {
                throw new IllegalArgumentException("Unit is mandatory");
            }

            repository.save(session, stone);
            tx.commit();
            return stone;

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
            throw new RuntimeException("Failed to create stone", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public Stone updateStone(Stone stone) {
        Transaction tx = null;
        Session session = null;
        try {
            session = AppConfig.getSessionFactory().openSession();
            tx = session.beginTransaction();
            repository.update(session, stone);
            tx.commit();
            return stone;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception ignore) {
                }
            }
            throw new RuntimeException("Failed to update stone", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void deactivateStone(Long id) {
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
            throw new RuntimeException("Failed to deactivate stone", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public List<Stone> listAllActive() {
        try (Session session = AppConfig.getSessionFactory().openSession()) {
            return repository.findAllActive(session);
        }
    }
}
