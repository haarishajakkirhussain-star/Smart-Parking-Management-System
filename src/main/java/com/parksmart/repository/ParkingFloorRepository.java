package com.parksmart.repository;

import com.parksmart.model.ParkingFloor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingFloorRepository extends JpaRepository<ParkingFloor, Long> {
    List<ParkingFloor> findAllByOrderByFloorNumberAsc();
    Optional<ParkingFloor> findByFloorNumber(Integer floorNumber);
}
