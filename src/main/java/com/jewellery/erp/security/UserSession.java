package com.jewellery.erp.security;

import com.jewellery.erp.model.UserRole;
import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicLong;

public class UserSession {

    private final Long userId;
    private final String username;
    private final UserRole role;
    private final LocalDateTime loginTime;
    private final String machineId;
    private final AtomicLong lastActivityMillis;

    private static final long TIMEOUT_MILLIS = 30 * 60 * 1000; // 30 minutes

    public UserSession(Long userId, String username, UserRole role, String machineId) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.machineId = machineId;
        this.loginTime = LocalDateTime.now();
        this.lastActivityMillis = new AtomicLong(System.currentTimeMillis());
    }

    public boolean isValid() {
        long elapsed = System.currentTimeMillis() - lastActivityMillis.get();
        return elapsed < TIMEOUT_MILLIS;
    }

    public void touch() {
        lastActivityMillis.set(System.currentTimeMillis());
    }

    public long getLastActivityMillis() {
        return lastActivityMillis.get();
    }

    // Getters
    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public UserRole getRole() {
        return role;
    }

    public String getMachineId() {
        return machineId;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

}
