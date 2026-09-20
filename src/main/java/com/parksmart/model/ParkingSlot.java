package com.parksmart.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "parking_slots", indexes = {
    @Index(name = "idx_slot_status", columnList = "status"),
    @Index(name = "idx_slot_type", columnList = "vehicle_type"),
    @Index(name = "idx_slot_code", columnList = "slot_code", unique = true)
})
public class ParkingSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "slot_number", nullable = false)
    private Integer slotNumber;

    @Column(name = "slot_code", nullable = false, unique = true, length = 20)
    private String slotCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false, length = 20)
    private VehicleType vehicleType;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SlotStatus status = SlotStatus.AVAILABLE;

    @Column(name = "is_occupied", nullable = false)
    private Boolean isOccupied = false;

    @Column(name = "current_vehicle_plate", length = 20)
    private String currentVehiclePlate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "zone_id", nullable = false)
    @JsonBackReference
    private ParkingZone zone;

    @Version
    private Long version;

    public ParkingSlot() {}

    public ParkingSlot(Integer slotNumber, String slotCode, VehicleType vehicleType, ParkingZone zone) {
        this.slotNumber = slotNumber;
        this.slotCode = slotCode;
        this.vehicleType = vehicleType;
        this.zone = zone;
        this.status = SlotStatus.AVAILABLE;
        this.isOccupied = false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getSlotNumber() {
        return slotNumber;
    }

    public void setSlotNumber(Integer slotNumber) {
        this.slotNumber = slotNumber;
    }

    public String getSlotCode() {
        return slotCode;
    }

    public void setSlotCode(String slotCode) {
        this.slotCode = slotCode;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public SlotStatus getStatus() {
        return status;
    }

    public void setStatus(SlotStatus status) {
        this.status = status;
        this.isOccupied = (status == SlotStatus.OCCUPIED);
    }

    public Boolean getIsOccupied() {
        return isOccupied;
    }

    public void setIsOccupied(Boolean occupied) {
        isOccupied = occupied;
        if (Boolean.TRUE.equals(occupied)) {
            this.status = SlotStatus.OCCUPIED;
        } else if (this.status == SlotStatus.OCCUPIED) {
            this.status = SlotStatus.AVAILABLE;
        }
    }

    public String getCurrentVehiclePlate() {
        return currentVehiclePlate;
    }

    public void setCurrentVehiclePlate(String currentVehiclePlate) {
        this.currentVehiclePlate = currentVehiclePlate;
    }

    public ParkingZone getZone() {
        return zone;
    }

    public void setZone(ParkingZone zone) {
        this.zone = zone;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
