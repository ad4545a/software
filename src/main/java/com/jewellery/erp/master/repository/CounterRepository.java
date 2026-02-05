package com.jewellery.erp.master.repository;

import com.jewellery.erp.master.entity.Counter;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.Optional;

public class CounterRepository extends BaseMasterRepository<Counter> {

    public CounterRepository() {
        super(Counter.class);
    }

    public Optional<Counter> findByName(Session session, String name) {
        String hql = "FROM Counter WHERE name = :name";
        Query<Counter> query = session.createQuery(hql, Counter.class);
        query.setParameter("name", name);
        return query.uniqueResultOptional();
    }

    public boolean isAssignedToUser(Session session, Long counterId) {
        String hql = "SELECT count(*) FROM UserProfile WHERE assignedCounter = :counterId AND active = true";
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("counterId", counterId);
        return query.uniqueResult() > 0;
    }
}
