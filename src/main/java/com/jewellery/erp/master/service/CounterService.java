package com.jewellery.erp.master.service;

import com.jewellery.erp.config.AppConfig;
import com.jewellery.erp.master.entity.Counter;
import com.jewellery.erp.master.repository.CounterRepository;
import com.jewellery.erp.master.repository.UserProfileRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class CounterService {

    private final CounterRepository repository = new CounterRepository();
    private final UserProfileRepository userProfileRepository = new UserProfileRepository();

    public Counter createCounter(Counter counter) {
        if (counter == null) {
            throw new IllegalArgumentException("Counter cannot be null");
        }

        Transaction tx = null;
        Session session = null;
        try {
            session = AppConfig.getSessionFactory().openSession();
            tx = session.beginTransaction();

            // Rule: Name must be unique
            if (repository.findByName(session, counter.getName()).isPresent()) {
                throw new IllegalArgumentException("Counter with name '" + counter.getName() + "' already exists");
            }

            repository.save(session, counter);
            tx.commit();
            return counter;

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
            throw new RuntimeException("Failed to create counter", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public Counter updateCounter(Counter counter) {
        Transaction tx = null;
        Session session = null;
        try {
            session = AppConfig.getSessionFactory().openSession();
            tx = session.beginTransaction();
            repository.update(session, counter);
            tx.commit();
            return counter;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                try {
                    tx.rollback();
                } catch (Exception ignore) {
                }
            }
            throw new RuntimeException("Failed to update counter", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void deactivateCounter(Long id) {
        Transaction tx = null;
        Session session = null;
        try {
            session = AppConfig.getSessionFactory().openSession();
            tx = session.beginTransaction();

            // Rule: Cannot deactivate if assigned to active UserProfile
            long assignmentCount = userProfileRepository.countByAssignedCounter(session, id);
            if (assignmentCount > 0) {
                throw new IllegalStateException(
                        "Cannot deactivate counter that is assigned to " + assignmentCount + " user profiles");
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
            throw new RuntimeException("Failed to deactivate counter", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public List<Counter> listAllActive() {
        try (Session session = AppConfig.getSessionFactory().openSession()) {
            return repository.findAllActive(session);
        }
    }
}
