package com.jewellery.erp.repository;

import com.jewellery.erp.model.LoginAudit;
import org.hibernate.Session;

public class LoginAuditRepository {

    public void save(Session session, LoginAudit audit) {
        session.persist(audit);
    }
}
