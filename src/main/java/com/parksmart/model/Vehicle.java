package com.parksmart.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "vehicles", indexes = {
    @Index(name = "idx_license_plate", columnList = "license_plate", unique = true)
})
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "license_plate", nullable = false, unique = true, length = 20)
    private String licensePlate;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 20)
    private VehicleType vehicleType;

    @Column(name = "owner_name", length = 100)
    private String ownerName;

    @Column(name = "mobile_number", length = 20)
    private String mobileNumber;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Vehicle() {}

    public Vehicle(String licensePlate, VehicleType vehicleType, String ownerName, String mobileNumber) {
        this.licensePlate = licensePlate != null ? licensePlate.trim().toUpperCase() : null;
        this.vehicleType = vehicleType;
        this.ownerName = ownerName;
        this.mobileNumber = mobileNumber;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate != null ? licensePlate.trim().toUpperCase() : null;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
