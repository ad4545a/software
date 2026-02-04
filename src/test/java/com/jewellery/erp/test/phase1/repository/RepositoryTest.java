package com.jewellery.erp.test.phase1.repository;

import com.jewellery.erp.config.AppConfig;
import com.jewellery.erp.model.User;
import com.jewellery.erp.model.UserRole;
import com.jewellery.erp.repository.UserRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class RepositoryTest {

    private static UserRepository userRepository;

    @BeforeAll
    public static void setup() {
        // Clean start
        File dbFile = new File("./data/jewellery.mv.db");
        if (dbFile.exists())
            dbFile.delete();

        AppConfig.initialize();
        userRepository = new UserRepository();
    }

    @Test
    public void testCreateAndFindUser() {
        User user = new User();
        user.setUsername("repo_test");
        user.setPasswordHash("hash123");
        user.setRole(UserRole.STAFF);
        user.setActive(true);

        try (Session session = AppConfig.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
        }

        try (Session session = AppConfig.getSessionFactory().openSession()) {
            Optional<User> fetched = userRepository.findByUsername(session, "repo_test");
            assertTrue(fetched.isPresent());
            assertEquals(UserRole.STAFF, fetched.get().getRole());
        }
    }

    @Test
    public void testDuplicateUsername() {
        User u1 = new User();
        u1.setUsername("duplicate");
        u1.setPasswordHash("hash");
        u1.setRole(UserRole.ADMIN);

        User u2 = new User();
        u2.setUsername("duplicate");
        u2.setPasswordHash("hash");
        u2.setRole(UserRole.STAFF);

        try (Session session = AppConfig.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(u1);
            tx.commit();
        }

        try (Session session = AppConfig.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            assertThrows(Exception.class, () -> {
                session.persist(u2);
                tx.commit();
            });
        }
    }
}
