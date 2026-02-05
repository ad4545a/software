package com.jewellery.erp.master.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "STONE")
public class Stone extends BaseMasterEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false, length = 20)
    private String unit;

    @Column(name = "rate_type", nullable = false, length = 20)
    private String rateType;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRateType() {
        return rateType;
    }

    public void setRateType(String rateType) {
        this.rateType = rateType;
    }
}
