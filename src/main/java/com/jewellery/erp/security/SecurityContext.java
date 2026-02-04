package com.jewellery.erp.security;

public class SecurityContext {

    private static UserSession currentSession;

    public static void setSession(UserSession session) {
        currentSession = session;
    }

    public static UserSession getSession() {
        if (currentSession != null && !currentSession.isValid()) {
            clear(); // Auto-clear if expired
        }
        return currentSession;
    }

    public static void clear() {
        currentSession = null;
    }

    public static boolean isAuthenticated() {
        return getSession() != null;
    }
}
