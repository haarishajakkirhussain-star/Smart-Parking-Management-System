package com.parksmart.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "parking_rates", indexes = {
    @Index(name = "idx_rate_vehicle_type", columnList = "vehicle_type", unique = true)
})
public class ParkingRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, unique = true, length = 20)
    private VehicleType vehicleType;

    @Column(name = "hourly_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal hourlyRate;

    @Column(name = "base_hours", nullable = false)
    private Integer baseHours = 1;

    @Column(name = "base_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal baseRate;

    @Column(name = "grace_period_minutes", nullable = false)
    private Integer gracePeriodMinutes = 15;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    public ParkingRate() {}

    public ParkingRate(VehicleType vehicleType, BigDecimal hourlyRate, Integer baseHours, BigDecimal baseRate, Integer gracePeriodMinutes) {
        this.vehicleType = vehicleType;
        this.hourlyRate = hourlyRate;
        this.baseHours = baseHours;
        this.baseRate = baseRate;
        this.gracePeriodMinutes = gracePeriodMinutes;
        this.isActive = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public Integer getBaseHours() {
        return baseHours;
    }

    public void setBaseHours(Integer baseHours) {
        this.baseHours = baseHours;
    }

    public BigDecimal getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(BigDecimal baseRate) {
        this.baseRate = baseRate;
    }

    public Integer getGracePeriodMinutes() {
        return gracePeriodMinutes;
    }

    public void setGracePeriodMinutes(Integer gracePeriodMinutes) {
        this.gracePeriodMinutes = gracePeriodMinutes;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
