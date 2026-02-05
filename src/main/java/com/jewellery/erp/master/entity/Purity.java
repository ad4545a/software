package com.jewellery.erp.master.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "PURITY", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "metal_id", "name" })
})
public class Purity extends BaseMasterEntity {

    @Column(name = "metal_id", nullable = false)
    private Long metalId;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Double percentage;

    // Getters and Setters
    public Long getMetalId() {
        return metalId;
    }

    public void setMetalId(Long metalId) {
        this.metalId = metalId;
    }

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
}
