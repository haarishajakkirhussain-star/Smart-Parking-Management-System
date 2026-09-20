package com.parksmart.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "parking_floors")
public class ParkingFloor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer floorNumber;

    @Column(nullable = false, length = 50)
    private String floorName;

    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<ParkingZone> zones = new ArrayList<>();

    public ParkingFloor() {}

    public ParkingFloor(Integer floorNumber, String floorName) {
        this.floorNumber = floorNumber;
        this.floorName = floorName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getFloorNumber() {
        return floorNumber;
    }

    public void setFloorNumber(Integer floorNumber) {
        this.floorNumber = floorNumber;
    }

    public String getFloorName() {
        return floorName;
    }

    public void setFloorName(String floorName) {
        this.floorName = floorName;
    }

    public List<ParkingZone> getZones() {
        return zones;
    }

    public void setZones(List<ParkingZone> zones) {
        this.zones = zones;
    }
}
