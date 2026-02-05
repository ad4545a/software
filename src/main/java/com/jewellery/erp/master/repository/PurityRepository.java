package com.jewellery.erp.master.repository;

import com.jewellery.erp.master.entity.Purity;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.List;
import java.util.Optional;

public class PurityRepository extends BaseMasterRepository<Purity> {

    public PurityRepository() {
        super(Purity.class);
    }

    public Optional<Purity> findByMetalAndName(Session session, Long metalId, String name) {
        String hql = "FROM Purity WHERE metalId = :metalId AND name = :name";
        Query<Purity> query = session.createQuery(hql, Purity.class);
        query.setParameter("metalId", metalId);
        query.setParameter("name", name);
        return query.uniqueResultOptional();
    }

    public List<Purity> findByMetal(Session session, Long metalId) {
        String hql = "FROM Purity WHERE metalId = :metalId AND active = true";
        Query<Purity> query = session.createQuery(hql, Purity.class);
        query.setParameter("metalId", metalId);
        return query.list();
    }

    public long countByMetal(Session session, Long metalId) {
        String hql = "SELECT count(*) FROM Purity WHERE metalId = :metalId";
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("metalId", metalId);
        return query.uniqueResult();
    }
}
