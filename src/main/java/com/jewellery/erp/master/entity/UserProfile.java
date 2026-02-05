package com.jewellery.erp.master.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "USER_PROFILE")
public class UserProfile extends BaseMasterEntity {

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    @Column(length = 15)
    private String mobile;

    @Column(name = "assigned_counter")
    private Long assignedCounter;

    // Getters and Setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Long getAssignedCounter() {
        return assignedCounter;
    }

    public void setAssignedCounter(Long assignedCounter) {
        this.assignedCounter = assignedCounter;
    }
}
