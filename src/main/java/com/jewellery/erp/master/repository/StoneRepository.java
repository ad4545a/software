package com.jewellery.erp.master.repository;

import com.jewellery.erp.master.entity.Stone;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.Optional;

public class StoneRepository extends BaseMasterRepository<Stone> {

    public StoneRepository() {
        super(Stone.class);
    }

    public Optional<Stone> findByName(Session session, String name) {
        String hql = "FROM Stone WHERE name = :name";
        Query<Stone> query = session.createQuery(hql, Stone.class);
        query.setParameter("name", name);
        return query.uniqueResultOptional();
    }
}
