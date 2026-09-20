package com.parksmart.dto;

import com.parksmart.model.VehicleType;
import java.time.LocalDateTime;

public class ParkingPassDTO {

    private String sessionCode;
    private String licensePlate;
    private VehicleType vehicleType;
    private String ownerName;
    private String slotCode;
    private String floorName;
    private String zoneName;
    private LocalDateTime entryTime;
    private String entryTimeFormatted;
    private String qrData;
    private String qrImageBase64;

    public ParkingPassDTO() {}

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

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
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

    public String getQrData() {
        return qrData;
    }

    public void setQrData(String qrData) {
        this.qrData = qrData;
    }

    public String getQrImageBase64() {
        return qrImageBase64;
    }

    public void setQrImageBase64(String qrImageBase64) {
        this.qrImageBase64 = qrImageBase64;
    }
}
