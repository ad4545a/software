package com.jewellery.erp.master.repository;

import com.jewellery.erp.master.entity.UserProfile;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.Optional;

public class UserProfileRepository extends BaseMasterRepository<UserProfile> {

    public UserProfileRepository() {
        super(UserProfile.class);
    }

    public Optional<UserProfile> findByUserId(Session session, Long userId) {
        String hql = "FROM UserProfile WHERE userId = :userId";
        Query<UserProfile> query = session.createQuery(hql, UserProfile.class);
        query.setParameter("userId", userId);
        return query.uniqueResultOptional();
    }

    public long countByAssignedCounter(Session session, Long counterId) {
        String hql = "SELECT count(*) FROM UserProfile WHERE assignedCounter = :counterId AND active = true";
        Query<Long> query = session.createQuery(hql, Long.class);
        query.setParameter("counterId", counterId);
        return query.uniqueResult();
    }
}
