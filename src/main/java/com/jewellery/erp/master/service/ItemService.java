package com.jewellery.erp.master.service;

import com.jewellery.erp.config.AppConfig;
import com.jewellery.erp.master.entity.Item;
import com.jewellery.erp.master.repository.ItemRepository;
import com.jewellery.erp.master.repository.MetalRepository;
import com.jewellery.erp.master.repository.PurityRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;

public class ItemService {

    private final ItemRepository repository = new ItemRepository();
    private final MetalRepository metalRepository = new MetalRepository();
    private final PurityRepository purityRepository = new PurityRepository();

    public Item createItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (item.getName() == null || item.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Item name is required");
        }

        Transaction tx = null;
        Session session = null;
        try {
            session = AppConfig.getSessionFactory().openSession();
            tx = session.beginTransaction();

            // Rule: Metal must exist (explicit check)
            if (item.getMetalId() == null) {
                throw new IllegalArgumentException("Metal is required");
            }
            var metal = metalRepository.findById(session, item.getMetalId())
                    .orElseThrow(() -> new IllegalArgumentException("Metal does not exist"));

            // Rule: If purity provided, it must belong to the selected metal (CRITICAL
            // CROSS-VALIDATION)
            if (item.getPurityId() != null) {
                var purity = purityRepository.findById(session, item.getPurityId())
                        .orElseThrow(() -> new IllegalArgumentException("Purity does not exist"));

                // 🔴 CRITICAL: Ensure purity belongs to selected metal
                if (!purity.getMetalId().equals(item.getMetalId())) {
                    throw new IllegalArgumentException("Purity does not belong to the selected metal");
                }
            }

            // Rule: Unique (name + metal + purity)
            if (repository.findByNameMetalPurity(session, item.getName(), item.getMetalId(), item.getPurityId())
                    .isPresent()) {
                throw new IllegalArgumentException("Item with this name, metal, and purity combination already exists");
            }

            // Rule: Wastage >= 0 (explicit validation BEFORE save)
            if (item.getWastagePercent() == null) {
                item.setWastagePercent(0.0); // Default to 0 if null
            }
            if (item.getWastagePercent() < 0) {
                throw new IllegalArgumentException("Wastage percentage cannot be negative");
            }

            repository.save(session, item);
            tx.commit();
            return item;

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
            throw new RuntimeException("Failed to create item", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public Item updateItem(Item item) {
        if (item == null) {
            throw new IllegalArgumentException("Item cannot be null");
        }
        if (item.getId() == null) {
            throw new IllegalArgumentException("Item ID is required for update");
        }
        Transaction tx = null;
        Session session = null;
        try {
            session = AppConfig.getSessionFactory().openSession();
            tx = session.beginTransaction();

            // Rule: Wastage >= 0
            if (item.getWastagePercent() < 0) {
                throw new IllegalArgumentException("Wastage percentage cannot be negative");
            }

            repository.update(session, item);
            tx.commit();
            return item;

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
            throw new RuntimeException("Failed to update item", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public void deactivateItem(Long id) {
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
            throw new RuntimeException("Failed to deactivate item", e);
        } finally {
            if (session != null && session.isOpen()) {
                session.close();
            }
        }
    }

    public List<Item> listAllActive() {
        try (Session session = AppConfig.getSessionFactory().openSession()) {
            return repository.findAllActive(session);
        }
    }
}
