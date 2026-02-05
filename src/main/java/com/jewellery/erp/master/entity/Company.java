package com.jewellery.erp.master.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "COMPANY")
public class Company extends BaseMasterEntity {

    @Column(nullable = false, unique = true, length = 200)
    private String name;

    @Column(length = 500)
    private String address;

    @Column(length = 20)
    private String phone;

    @Column(name = "gst_number", length = 15)
    private String gstNumber;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }
}
