package com.parksmart.service;

import com.parksmart.dto.*;
import com.parksmart.exception.DuplicateActiveSessionException;
import com.parksmart.exception.ResourceNotFoundException;
import com.parksmart.exception.SessionNotFoundException;
import com.parksmart.model.*;
import com.parksmart.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class ParkingSessionService {

    private static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy hh:mm a");

    private final VehicleRepository vehicleRepository;
    private final ParkingSessionRepository sessionRepository;
    private final PaymentRepository paymentRepository;
    private final SlotAllocationService slotAllocationService;
    private final PricingService pricingService;
    private final QRCodeService qrCodeService;

    public ParkingSessionService(VehicleRepository vehicleRepository,
                                 ParkingSessionRepository sessionRepository,
                                 PaymentRepository paymentRepository,
                                 SlotAllocationService slotAllocationService,
                                 PricingService pricingService,
                                 QRCodeService qrCodeService) {
        this.vehicleRepository = vehicleRepository;
        this.sessionRepository = sessionRepository;
        this.paymentRepository = paymentRepository;
        this.slotAllocationService = slotAllocationService;
        this.pricingService = pricingService;
        this.qrCodeService = qrCodeService;
    }

    /**
     * Vehicle Entry & Automatic Allocation Workflow
     */
    @Transactional
    public ParkingPassDTO registerVehicleEntry(VehicleEntryRequest request) {
        String cleanPlate = request.getLicensePlate();

        // 1. Guard against duplicate active sessions
        Optional<ParkingSession> existingActive = sessionRepository.findActiveSessionByLicensePlate(cleanPlate);
        if (existingActive.isPresent()) {
            throw new DuplicateActiveSessionException("Vehicle " + cleanPlate + " already has an active parking session in bay "
                    + existingActive.get().getSlot().getSlotCode());
        }

        // 2. Find or register vehicle
        Vehicle vehicle = vehicleRepository.findByLicensePlateIgnoreCase(cleanPlate)
                .orElseGet(() -> new Vehicle(cleanPlate, request.getVehicleType(), request.getOwnerName(), request.getMobileNumber()));

        vehicle.setVehicleType(request.getVehicleType());
        if (request.getOwnerName() != null && !request.getOwnerName().isBlank()) {
            vehicle.setOwnerName(request.getOwnerName().trim());
        }
        if (request.getMobileNumber() != null && !request.getMobileNumber().isBlank()) {
            vehicle.setMobileNumber(request.getMobileNumber().trim());
        }
        vehicle = vehicleRepository.save(vehicle);

        // 3. Atomically allocate compatible parking slot
        ParkingSlot allocatedSlot = slotAllocationService.allocateSlot(
                request.getVehicleType(),
                cleanPlate,
                request.getPreferredFloorId(),
                request.getPreferredZoneId()
        );

        // 4. Create and persist ParkingSession
        ParkingSession session = new ParkingSession(vehicle, allocatedSlot);
        session = sessionRepository.save(session);

        // 5. Generate QR Code containing parking session information
        String qrPayload = String.format("{\"session\":\"%s\",\"plate\":\"%s\",\"bay\":\"%s\",\"floor\":\"%s\",\"zone\":\"%s\",\"entry\":\"%s\"}",
                session.getSessionCode(),
                vehicle.getLicensePlate(),
                allocatedSlot.getSlotCode(),
                allocatedSlot.getZone().getFloor().getFloorName(),
                allocatedSlot.getZone().getZoneName(),
                session.getEntryTime().format(DISPLAY_FORMATTER));

        String qrBase64 = qrCodeService.generateQRCodeBase64(qrPayload, 260, 260);

        // 6. Build response DTO
        ParkingPassDTO pass = new ParkingPassDTO();
        pass.setSessionCode(session.getSessionCode());
        pass.setLicensePlate(vehicle.getLicensePlate());
        pass.setVehicleType(vehicle.getVehicleType());
        pass.setOwnerName(vehicle.getOwnerName());
        pass.setSlotCode(allocatedSlot.getSlotCode());
        pass.setFloorName(allocatedSlot.getZone().getFloor().getFloorName());
        pass.setZoneName(allocatedSlot.getZone().getZoneName());
        pass.setEntryTime(session.getEntryTime());
        pass.setEntryTimeFormatted(session.getEntryTime().format(DISPLAY_FORMATTER));
        pass.setQrData(qrPayload);
        pass.setQrImageBase64(qrBase64);

        return pass;
    }

    /**
     * Preview fee and session details for Checkout before payment
     */
    @Transactional(readOnly = true)
    public CheckoutPreviewDTO previewCheckout(String licensePlate) {
        String cleanPlate = licensePlate != null ? licensePlate.trim().toUpperCase().replaceAll("\\s+", "") : "";
        ParkingSession session = sessionRepository.findActiveSessionByLicensePlate(cleanPlate)
                .orElseThrow(() -> new SessionNotFoundException("No active parking session found for license plate: " + cleanPlate));

        LocalDateTime now = LocalDateTime.now();
        PricingService.CalculationResult calculation = pricingService.calculateFee(
                session.getVehicle().getVehicleType(),
                session.getEntryTime(),
                now
        );

        CheckoutPreviewDTO preview = new CheckoutPreviewDTO();
        preview.setSessionCode(session.getSessionCode());
        preview.setLicensePlate(session.getVehicle().getLicensePlate());
        preview.setVehicleType(session.getVehicle().getVehicleType());
        preview.setSlotCode(session.getSlot().getSlotCode());
        preview.setFloorName(session.getSlot().getZone().getFloor().getFloorName());
        preview.setZoneName(session.getSlot().getZone().getZoneName());
        preview.setEntryTime(session.getEntryTime());
        preview.setEntryTimeFormatted(session.getEntryTime().format(DISPLAY_FORMATTER));
        preview.setCheckoutTime(now);
        preview.setCheckoutTimeFormatted(now.format(DISPLAY_FORMATTER));
        preview.setDurationMinutes(calculation.getDurationMinutes());
        preview.setDurationFormatted(formatDuration(calculation.getDurationMinutes()));
        preview.setHourlyRate(calculation.getRate().getHourlyRate());
        preview.setBaseRate(calculation.getRate().getBaseRate());
        preview.setGracePeriodMinutes(calculation.getRate().getGracePeriodMinutes());
        preview.setCalculationExplanation(calculation.getExplanation());
        preview.setTotalFee(calculation.getTotalFee());

        return preview;
    }

    /**
     * Complete Vehicle Checkout and Process Payment
     */
    @Transactional
    public CheckoutResponse completeCheckout(CheckoutRequest request) {
        String cleanPlate = request.getLicensePlate();
        ParkingSession session = sessionRepository.findActiveSessionByLicensePlate(cleanPlate)
                .orElseThrow(() -> new SessionNotFoundException("No active parking session found for license plate: " + cleanPlate));

        LocalDateTime exitTime = LocalDateTime.now();
        PricingService.CalculationResult calculation = pricingService.calculateFee(
                session.getVehicle().getVehicleType(),
                session.getEntryTime(),
                exitTime
        );

        // 1. Update session
        session.setExitTime(exitTime);
        session.setDurationMinutes(calculation.getDurationMinutes());
        session.setTotalFee(calculation.getTotalFee());
        session.setStatus(SessionStatus.COMPLETED);
        sessionRepository.save(session);

        // 2. Record Payment with confirmed status
        Payment payment = new Payment(
                session,
                calculation.getTotalFee(),
                request.getPaymentMethod(),
                PaymentStatus.PAID
        );
        paymentRepository.save(payment);

        // 3. Free the parking slot
        slotAllocationService.releaseSlot(session.getSlot());

        // 4. Construct response
        CheckoutResponse response = new CheckoutResponse();
        response.setSessionCode(session.getSessionCode());
        response.setLicensePlate(session.getVehicle().getLicensePlate());
        response.setVehicleType(session.getVehicle().getVehicleType());
        response.setSlotCode(session.getSlot().getSlotCode());
        response.setEntryTime(session.getEntryTime());
        response.setExitTime(exitTime);
        response.setDurationMinutes(calculation.getDurationMinutes());
        response.setDurationFormatted(formatDuration(calculation.getDurationMinutes()));
        response.setTotalFee(calculation.getTotalFee());
        response.setPaymentMethod(request.getPaymentMethod());
        response.setTransactionRef(payment.getTransactionRef());
        response.setPaymentStatus(PaymentStatus.PAID.name());

        return response;
    }

    public Page<ParkingSession> getRecentSessions(int page, int size) {
        return sessionRepository.findAllByOrderByEntryTimeDesc(PageRequest.of(page, size));
    }

    private String formatDuration(long minutes) {
        if (minutes < 60) {
            return minutes + " mins";
        }
        long hours = minutes / 60;
        long remainingMins = minutes % 60;
        return hours + " hr " + remainingMins + " mins";
    }
}
