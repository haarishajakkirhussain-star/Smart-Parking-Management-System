package com.parksmart.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parking_zones")
public class ParkingZone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String zoneCode;

    @Column(nullable = false, length = 50)
    private String zoneName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    @JsonBackReference
    private ParkingFloor floor;

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ParkingSlot> slots = new ArrayList<>();

    public ParkingZone() {}

    public ParkingZone(String zoneCode, String zoneName, ParkingFloor floor) {
        this.zoneCode = zoneCode;
        this.zoneName = zoneName;
        this.floor = floor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getZoneCode() {
        return zoneCode;
    }

    public void setZoneCode(String zoneCode) {
        this.zoneCode = zoneCode;
    }

    public String getZoneName() {
        return zoneName;
    }

    public void setZoneName(String zoneName) {
        this.zoneName = zoneName;
    }

    public ParkingFloor getFloor() {
        return floor;
    }

    public void setFloor(ParkingFloor floor) {
        this.floor = floor;
    }

    public List<ParkingSlot> getSlots() {
        return slots;
    }

    public void setSlots(List<ParkingSlot> slots) {
        this.slots = slots;
    }
}
