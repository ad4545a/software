package com.jewellery.erp.master.repository;

import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;
import java.util.Optional;

public abstract class BaseMasterRepository<T> {

    private final Class<T> entityClass;

    protected BaseMasterRepository(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public void save(Session session, T entity) {
        session.persist(entity);
    }

    public void update(Session session, T entity) {
        session.merge(entity);
    }

    public Optional<T> findById(Session session, Long id) {
        T entity = session.get(entityClass, id);
        return Optional.ofNullable(entity);
    }

    public List<T> findAll(Session session) {
        String hql = "FROM " + entityClass.getSimpleName();
        Query<T> query = session.createQuery(hql, entityClass);
        return query.list();
    }

    public List<T> findAllActive(Session session) {
        String hql = "FROM " + entityClass.getSimpleName() + " WHERE active = true";
        Query<T> query = session.createQuery(hql, entityClass);
        return query.list();
    }

    public void deactivate(Session session, Long id) {
        String hql = "UPDATE " + entityClass.getSimpleName() + " SET active = false WHERE id = :id";
        session.createMutationQuery(hql)
                .setParameter("id", id)
                .executeUpdate();
    }
}
