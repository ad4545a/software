package com.jewellery.erp.master.repository;

import com.jewellery.erp.master.entity.Company;
import org.hibernate.Session;
import org.hibernate.query.Query;
import java.util.Optional;

public class CompanyRepository extends BaseMasterRepository<Company> {

    public CompanyRepository() {
        super(Company.class);
    }

    public Optional<Company> findByName(Session session, String name) {
        String hql = "FROM Company WHERE name = :name";
        Query<Company> query = session.createQuery(hql, Company.class);
        query.setParameter("name", name);
        return query.uniqueResultOptional();
    }

    public Optional<Company> findActiveCompany(Session session) {
        String hql = "FROM Company WHERE active = true";
        Query<Company> query = session.createQuery(hql, Company.class);
        query.setMaxResults(1);
        return query.uniqueResultOptional();
    }

    public long countActiveCompanies(Session session) {
        String hql = "SELECT count(*) FROM Company WHERE active = true";
        Query<Long> query = session.createQuery(hql, Long.class);
        return query.uniqueResult();
    }
}
