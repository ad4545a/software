package com.jewellery.erp.master.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "TAX")
public class Tax extends BaseMasterEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false)
    private Double percentage;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
}
