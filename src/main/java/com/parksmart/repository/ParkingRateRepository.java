package com.parksmart.repository;

import com.parksmart.model.ParkingRate;
import com.parksmart.model.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ParkingRateRepository extends JpaRepository<ParkingRate, Long> {
    Optional<ParkingRate> findByVehicleTypeAndIsActiveTrue(VehicleType vehicleType);
    Optional<ParkingRate> findByVehicleType(VehicleType vehicleType);
}
