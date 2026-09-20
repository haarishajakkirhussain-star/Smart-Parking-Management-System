package com.parksmart.dto;

import com.parksmart.model.VehicleType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CheckoutPreviewDTO {

    private String sessionCode;
    private String licensePlate;
    private VehicleType vehicleType;
    private String slotCode;
    private String floorName;
    private String zoneName;
    private LocalDateTime entryTime;
    private String entryTimeFormatted;
    private LocalDateTime checkoutTime;
    private String checkoutTimeFormatted;
    private Long durationMinutes;
    private String durationFormatted;
    private BigDecimal hourlyRate;
    private BigDecimal baseRate;
    private Integer gracePeriodMinutes;
    private String calculationExplanation;
    private BigDecimal totalFee;

    public CheckoutPreviewDTO() {}

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getSlotCode() {
        return slotCode;
    }

    public void setSlotCode(String slotCode) {
        this.slotCode = slotCode;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public String getEntryTimeFormatted() {
        return entryTimeFormatted;
    }

    public void setEntryTimeFormatted(String entryTimeFormatted) {
        this.entryTimeFormatted = entryTimeFormatted;
    }

    public LocalDateTime getCheckoutTime() {
        return checkoutTime;
    }

    public void setCheckoutTime(LocalDateTime checkoutTime) {
        this.checkoutTime = checkoutTime;
    }

    public String getCheckoutTimeFormatted() {
        return checkoutTimeFormatted;
    }

    public void setCheckoutTimeFormatted(String checkoutTimeFormatted) {
        this.checkoutTimeFormatted = checkoutTimeFormatted;
    }

    public Long getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public String getDurationFormatted() {
        return durationFormatted;
    }

    public void setDurationFormatted(String durationFormatted) {
        this.durationFormatted = durationFormatted;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
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

    public String getCalculationExplanation() {
        return calculationExplanation;
    }

    public void setCalculationExplanation(String calculationExplanation) {
        this.calculationExplanation = calculationExplanation;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }
}
