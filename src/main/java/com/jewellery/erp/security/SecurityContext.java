package com.jewellery.erp.security;

import java.util.concurrent.atomic.AtomicReference;

public final class SecurityContext {

    private static final AtomicReference<UserSession> SESSION = new AtomicReference<>();

    public static void setSession(UserSession session) {
        SESSION.set(session);
    }

    public static UserSession getSession() {
        UserSession session = SESSION.get();
        if (session != null && !session.isValid()) {
            clear(); // Auto-clear if expired
        }
        return SESSION.get();
    }

    public static boolean isAuthenticated() {
        return getSession() != null;
    }

    public static void clear() {
        SESSION.set(null);
    }
}
