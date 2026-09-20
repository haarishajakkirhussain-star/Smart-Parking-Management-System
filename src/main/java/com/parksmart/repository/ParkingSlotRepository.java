package com.parksmart.repository;

import com.parksmart.model.ParkingSlot;
import com.parksmart.model.SlotStatus;
import com.parksmart.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {

    Optional<ParkingSlot> findBySlotCode(String slotCode);

    List<ParkingSlot> findByStatus(SlotStatus status);

    List<ParkingSlot> findByVehicleTypeAndStatus(VehicleType vehicleType, SlotStatus status);

    long countByStatus(SlotStatus status);

    long countByVehicleTypeAndStatus(VehicleType vehicleType, SlotStatus status);

    // Find slots by floor ID through zone
    @Query("SELECT s FROM ParkingSlot s WHERE s.zone.floor.id = :floorId ORDER BY s.slotNumber ASC")
    List<ParkingSlot> findByFloorId(@Param("floorId") Long floorId);

    // Find available slot prioritizing floor preference and zone preference
    @Query("SELECT s FROM ParkingSlot s WHERE s.status = 'AVAILABLE' AND s.vehicleType = :vehicleType " +
           "AND (:floorId IS NULL OR s.zone.floor.id = :floorId) " +
           "AND (:zoneId IS NULL OR s.zone.id = :zoneId) " +
           "ORDER BY s.zone.floor.floorNumber ASC, s.zone.zoneCode ASC, s.slotNumber ASC")
    List<ParkingSlot> findAvailableSlotsWithPreference(
            @Param("vehicleType") VehicleType vehicleType,
            @Param("floorId") Long floorId,
            @Param("zoneId") Long zoneId);

    // Fallback: any available slot compatible with vehicle type
    @Query("SELECT s FROM ParkingSlot s WHERE s.status = 'AVAILABLE' AND s.vehicleType = :vehicleType " +
           "ORDER BY s.zone.floor.floorNumber ASC, s.zone.zoneCode ASC, s.slotNumber ASC")
    List<ParkingSlot> findAllAvailableByVehicleType(@Param("vehicleType") VehicleType vehicleType);

    // Atomic update to guarantee no two threads/requests can grab the same slot
    @Modifying
    @Query("UPDATE ParkingSlot s SET s.status = :newStatus, s.isOccupied = :isOccupied, s.currentVehiclePlate = :plate " +
           "WHERE s.id = :slotId AND s.status = :expectedStatus")
    int updateSlotStatusIfMatches(
            @Param("slotId") Long slotId,
            @Param("newStatus") SlotStatus newStatus,
            @Param("isOccupied") Boolean isOccupied,
            @Param("plate") String plate,
            @Param("expectedStatus") SlotStatus expectedStatus);
}
