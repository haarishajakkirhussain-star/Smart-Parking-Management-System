package com.parksmart.dto;

import com.parksmart.model.VehicleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class VehicleEntryRequest {

    @NotBlank(message = "License plate number is required")
    @Pattern(regexp = "^[A-Za-z0-9 -]{4,15}$", message = "Invalid license plate format (e.g. MH12AB1234 or KA-01-E-1234)")
    private String licensePlate;

    private String ownerName;

    @Pattern(regexp = "^$|^[0-9]{10}$", message = "Mobile number must be a valid 10-digit number")
    private String mobileNumber;

    @NotNull(message = "Vehicle type is required (TWO_WHEELER or FOUR_WHEELER)")
    private VehicleType vehicleType;

    private Long preferredFloorId;
    private Long preferredZoneId;

    public VehicleEntryRequest() {}

    public String getLicensePlate() {
        return licensePlate != null ? licensePlate.trim().toUpperCase().replaceAll("\\s+", "") : null;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
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

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public Long getPreferredFloorId() {
        return preferredFloorId;
    }

    public void setPreferredFloorId(Long preferredFloorId) {
        this.preferredFloorId = preferredFloorId;
    }

    public Long getPreferredZoneId() {
        return preferredZoneId;
    }

    public void setPreferredZoneId(Long preferredZoneId) {
        this.preferredZoneId = preferredZoneId;
    }
}
