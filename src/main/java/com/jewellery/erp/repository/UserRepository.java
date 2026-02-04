package com.jewellery.erp.repository;

import com.jewellery.erp.model.User;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.Optional;

public class UserRepository {

    public Optional<User> findByUsername(Session session, String username) {
        Query<User> query = session.createQuery("FROM User u WHERE u.username = :username", User.class);
        query.setParameter("username", username);
        return query.uniqueResultOptional();
    }

    public void save(Session session, User user) {
        session.persist(user);
    }
}
