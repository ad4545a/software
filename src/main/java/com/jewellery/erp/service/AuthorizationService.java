package com.jewellery.erp.service;

import com.jewellery.erp.model.Feature;
import com.jewellery.erp.model.UserRole;
import com.jewellery.erp.security.AuthException;
import com.jewellery.erp.security.SecurityContext;
import com.jewellery.erp.security.UserSession;

public class AuthorizationService {

    public void checkPermission(Feature feature) {
        UserSession session = SecurityContext.getSession();
        if (session == null || !session.isValid()) {
            throw new AuthException("Session Expired or Invalid");
        }

        // Update activity timestamp
        session.touch();

        if (!hasPermission(session.getRole(), feature)) {
            throw new AuthException("Access Denied: You do not have permission for " + feature);
        }
    }

    public boolean hasPermission(UserRole role, Feature feature) {
        if (role == UserRole.ADMIN)
            return true;

        switch (role) {
            case STAFF:
                return feature == Feature.BILLING;

            case ACCOUNTANT:
                return feature == Feature.REPORTS;

            default:
                return false;
        }
    }
}
