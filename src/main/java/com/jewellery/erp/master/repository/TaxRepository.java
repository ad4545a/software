package com.jewellery.erp.master.repository;

import com.jewellery.erp.master.entity.Tax;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.Optional;

public class TaxRepository extends BaseMasterRepository<Tax> {

    public TaxRepository() {
        super(Tax.class);
    }

    public Optional<Tax> findByName(Session session, String name) {
        String hql = "FROM Tax WHERE name = :name";
        Query<Tax> query = session.createQuery(hql, Tax.class);
        query.setParameter("name", name);
        return query.uniqueResultOptional();
    }

    public Optional<Tax> findDefault(Session session) {
        String hql = "FROM Tax WHERE isDefault = true AND active = true";
        Query<Tax> query = session.createQuery(hql, Tax.class);
        query.setMaxResults(1);
        return query.uniqueResultOptional();
    }

    public long countDefaultTaxes(Session session) {
        String hql = "SELECT count(*) FROM Tax WHERE isDefault = true AND active = true";
        Query<Long> query = session.createQuery(hql, Long.class);
        return query.uniqueResult();
    }
}
