package com.parksmart.repository;

import com.parksmart.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    Optional<Vehicle> findByLicensePlateIgnoreCase(String licensePlate);
    boolean existsByLicensePlateIgnoreCase(String licensePlate);
}
