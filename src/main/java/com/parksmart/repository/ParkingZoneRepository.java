package com.parksmart.repository;

import com.parksmart.model.ParkingZone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingZoneRepository extends JpaRepository<ParkingZone, Long> {
    List<ParkingZone> findByFloorId(Long floorId);
    Optional<ParkingZone> findByFloorIdAndZoneCode(Long floorId, String zoneCode);
}
