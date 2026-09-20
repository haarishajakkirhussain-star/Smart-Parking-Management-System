package com.parksmart.controller;

import com.parksmart.dto.ApiResponse;
import com.parksmart.dto.CheckoutPreviewDTO;
import com.parksmart.dto.CheckoutRequest;
import com.parksmart.dto.CheckoutResponse;
import com.parksmart.model.ParkingSession;
import com.parksmart.service.ParkingSessionService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parking")
@CrossOrigin(origins = "*")
public class ParkingCheckoutController {

    private final ParkingSessionService sessionService;

    public ParkingCheckoutController(ParkingSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @GetMapping("/active/{plateNumber}")
    public ApiResponse<CheckoutPreviewDTO> getActiveSessionForCheckout(@PathVariable String plateNumber) {
        CheckoutPreviewDTO preview = sessionService.previewCheckout(plateNumber);
        return ApiResponse.success("Active parking session found", preview);
    }

    @PostMapping("/checkout")
    public ApiResponse<CheckoutResponse> processCheckout(@Valid @RequestBody CheckoutRequest request) {
        CheckoutResponse response = sessionService.completeCheckout(request);
        return ApiResponse.success("Vehicle checkout completed and payment recorded successfully", response);
    }

    @GetMapping("/history")
    public ApiResponse<Page<ParkingSession>> getParkingHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size) {
        Page<ParkingSession> history = sessionService.getRecentSessions(page, size);
        return ApiResponse.success("Parking session history retrieved", history);
    }
}
