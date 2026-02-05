package com.jewellery.erp.master.repository;

import com.jewellery.erp.master.entity.Metal;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.Optional;

public class MetalRepository extends BaseMasterRepository<Metal> {

    public MetalRepository() {
        super(Metal.class);
    }

    public Optional<Metal> findByName(Session session, String name) {
        String hql = "FROM Metal WHERE name = :name";
        Query<Metal> query = session.createQuery(hql, Metal.class);
        query.setParameter("name", name);
        return query.uniqueResultOptional();
    }

    public Optional<Metal> findByCode(Session session, String code) {
        String hql = "FROM Metal WHERE code = :code";
        Query<Metal> query = session.createQuery(hql, Metal.class);
        query.setParameter("code", code);
        return query.uniqueResultOptional();
    }

    public boolean isReferencedByPurity(Session session, Long metalId) {
        String hql = "SELECT count(*) FROM Purity WHERE metalId = :metalId";
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("metalId", metalId);
        return query.uniqueResult() > 0;
    }
}
