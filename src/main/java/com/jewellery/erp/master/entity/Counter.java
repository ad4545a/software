package com.jewellery.erp.master.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "COUNTER")
public class Counter extends BaseMasterEntity {

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 200)
    private String location;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
