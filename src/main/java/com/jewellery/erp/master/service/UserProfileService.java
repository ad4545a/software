package com.jewellery.erp.master.service;

import com.jewellery.erp.config.AppConfig;
import com.jewellery.erp.master.entity.UserProfile;
import com.jewellery.erp.master.repository.UserProfileRepository;
import com.jewellery.erp.master.repository.CounterRepository;
import com.jewellery.erp.security.SecurityContext;
import com.jewellery.erp.security.UserSession;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class UserProfileService {

    private final UserProfileRepository repository = new UserProfileRepository();
    private final CounterRepository counterRepository = new CounterRepository();

    public UserProfile createProfile(UserProfile profile) {
        if (profile == null) {
            throw new IllegalArgumentException("UserProfile cannot be null");
        }
        Transaction tx = null;
        Session session = null;
        try {
            session = AppConfig.getSessionFactory().openSession();
            tx = session.beginTransaction();

            // Rule: Cannot assign inactive counter
            if (profile.getAssignedCounter() != null) {
                validateCounterActive(session, profile.getAssignedCounter());
            }

            repository.save(session, profile);
            tx.commit();
            return profile;

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
            throw new RuntimeException("Failed to create user profile", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public UserProfile updateProfile(UserProfile profile) {
        Transaction tx = null;
        Session session = null;
        try {
            session = AppConfig.getSessionFactory().openSession();
            tx = session.beginTransaction();

            // Rule: Cannot assign inactive counter
            if (profile.getAssignedCounter() != null) {
                validateCounterActive(session, profile.getAssignedCounter());
            }

            repository.update(session, profile);
            tx.commit();
            return profile;

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
            throw new RuntimeException("Failed to update user profile", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void deactivateProfile(Long id) {
        Transaction tx = null;
        Session session = null;
        try {
            session = AppConfig.getSessionFactory().openSession();
            tx = session.beginTransaction();

            // Fetch profile first
            UserProfile profile = repository.findById(session, id)
                    .orElseThrow(() -> new IllegalArgumentException("Profile not found"));

            // Rule: Cannot deactivate logged-in user (compare userId, not object)
            UserSession currentSession = SecurityContext.getSession();
            if (currentSession != null) {
                // 🔴 CRITICAL FIX: Compare userId values, not object references
                if (profile.getUserId().equals(currentSession.getUserId())) {
                    throw new IllegalStateException("Cannot deactivate your own profile while logged in");
                }
            }

            repository.deactivate(session, id);
            tx.commit();

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
            throw new RuntimeException("Failed to deactivate user profile", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public List<UserProfile> listAllActive() {
        try (Session session = AppConfig.getSessionFactory().openSession()) {
            return repository.findAllActive(session);
        }
    }

    public UserProfile findByUserId(Long userId) {
        try (Session session = AppConfig.getSessionFactory().openSession()) {
            return repository.findByUserId(session, userId).orElse(null);
        }
    }

    private void validateCounterActive(Session session, Long counterId) {
        counterRepository.findById(session, counterId)
                .filter(c -> c.getActive())
                .orElseThrow(() -> new IllegalArgumentException("Counter does not exist or is inactive"));
    }
}
