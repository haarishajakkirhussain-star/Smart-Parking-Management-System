package com.parksmart.controller;

import com.parksmart.dto.ApiResponse;
import com.parksmart.dto.ParkingPassDTO;
import com.parksmart.dto.VehicleEntryRequest;
import com.parksmart.service.ParkingSessionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parking")
@CrossOrigin(origins = "*")
public class ParkingEntryController {

    private final ParkingSessionService sessionService;

    public ParkingEntryController(ParkingSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping("/entry")
    public ResponseEntity<ApiResponse<ParkingPassDTO>> registerEntry(@Valid @RequestBody VehicleEntryRequest request) {
        ParkingPassDTO pass = sessionService.registerVehicleEntry(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vehicle checked in successfully. Bay allocated: " + pass.getSlotCode(), pass));
    }
}
