package com.jewellery.erp.master.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ITEM", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "name", "metal_id", "purity_id" })
})
public class Item extends BaseMasterEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(name = "metal_id", nullable = false)
    private Long metalId;

    @Column(name = "purity_id")
    private Long purityId;

    @Column(name = "wastage_percent", nullable = false)
    private Double wastagePercent = 0.0;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getMetalId() {
        return metalId;
    }

    public void setMetalId(Long metalId) {
        this.metalId = metalId;
    }

    public Long getPurityId() {
        return purityId;
    }

    public void setPurityId(Long purityId) {
        this.purityId = purityId;
    }

    public Double getWastagePercent() {
        return wastagePercent;
    }

    public void setWastagePercent(Double wastagePercent) {
        this.wastagePercent = wastagePercent;
    }
}
