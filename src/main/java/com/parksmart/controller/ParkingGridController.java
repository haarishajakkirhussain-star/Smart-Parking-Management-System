package com.parksmart.controller;

import com.parksmart.dto.ApiResponse;
import com.parksmart.model.ParkingFloor;
import com.parksmart.model.ParkingSlot;
import com.parksmart.repository.ParkingFloorRepository;
import com.parksmart.repository.ParkingSlotRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class ParkingGridController {

    private final ParkingFloorRepository floorRepository;
    private final ParkingSlotRepository slotRepository;

    public ParkingGridController(ParkingFloorRepository floorRepository, ParkingSlotRepository slotRepository) {
        this.floorRepository = floorRepository;
        this.slotRepository = slotRepository;
    }

    @GetMapping("/floors")
    public ApiResponse<List<ParkingFloor>> getAllFloors() {
        List<ParkingFloor> floors = floorRepository.findAllByOrderByFloorNumberAsc();
        return ApiResponse.success("Floors and zones retrieved successfully", floors);
    }

    @GetMapping("/grid/slots")
    public ApiResponse<List<ParkingSlot>> getSlotsByFloor(@RequestParam(required = false) Long floorId) {
        List<ParkingSlot> slots = (floorId != null)
                ? slotRepository.findByFloorId(floorId)
                : slotRepository.findAll();
        return ApiResponse.success("Parking slots retrieved successfully", slots);
    }
}
