package com.parksmart.controller;

import com.parksmart.dto.ApiResponse;
import com.parksmart.dto.ParkingRateUpdateDTO;
import com.parksmart.exception.ResourceNotFoundException;
import com.parksmart.model.ParkingRate;
import com.parksmart.repository.ParkingRateRepository;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rates")
@CrossOrigin(origins = "*")
public class ParkingRateController {

    private final ParkingRateRepository rateRepository;

    public ParkingRateController(ParkingRateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }

    @GetMapping
    public ApiResponse<List<ParkingRate>> getAllRates() {
        return ApiResponse.success("Parking rates retrieved", rateRepository.findAll());
    }

    @PutMapping("/{id}")
    public ApiResponse<ParkingRate> updateRate(@PathVariable Long id, @Valid @RequestBody ParkingRateUpdateDTO dto) {
        ParkingRate rate = rateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rate rule with ID " + id + " not found"));

        rate.setHourlyRate(dto.getHourlyRate());
        rate.setBaseRate(dto.getBaseRate());
        rate.setBaseHours(dto.getBaseHours());
        rate.setGracePeriodMinutes(dto.getGracePeriodMinutes());

        ParkingRate saved = rateRepository.save(rate);
        return ApiResponse.success("Parking rate updated successfully", saved);
    }
}
