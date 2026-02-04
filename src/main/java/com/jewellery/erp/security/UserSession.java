package com.jewellery.erp.security;

import com.jewellery.erp.model.UserRole;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class UserSession {

    private final Long userId;
    private final String username;
    private final UserRole role;
    private final LocalDateTime loginTime;
    private final String machineId;
    private LocalDateTime lastActivity;

    private static final int TIMEOUT_MINUTES = 30;

    public UserSession(Long userId, String username, UserRole role, String machineId) {
        this.userId = userId;
        this.username = username;
        this.role = role;
        this.machineId = machineId;
        this.loginTime = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
    }

    public boolean isValid() {
        long minutes = ChronoUnit.MINUTES.between(lastActivity, LocalDateTime.now());
        return minutes < TIMEOUT_MINUTES;
    }

    public void touch() {
        this.lastActivity = LocalDateTime.now();
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

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }
}
