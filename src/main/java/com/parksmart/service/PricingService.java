
package com.parksmart.service;

import com.parksmart.exception.ResourceNotFoundException;
import com.parksmart.model.ParkingRate;
import com.parksmart.model.VehicleType;
import com.parksmart.repository.ParkingRateRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class PricingService {

    private final ParkingRateRepository rateRepository;

    public PricingService(ParkingRateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }

    public ParkingRate getRateForVehicleType(VehicleType vehicleType) {
        return rateRepository.findByVehicleTypeAndIsActiveTrue(vehicleType)
                .orElseGet(() -> rateRepository.findByVehicleType(vehicleType)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "No parking rate configured for " + vehicleType)));
    }

    /**
     * Billing rules:
     * 1. Minimum charge is one full hour.
     * 2. Any additional time is rounded up to the next whole hour.
     * 3. The configured hourly rate is based on vehicle type.
     * 4. Grace period does not make the parking fee zero.
     */
    public CalculationResult calculateFee(
            VehicleType vehicleType,
            LocalDateTime entryTime,
            LocalDateTime exitTime) {

        ParkingRate rate = getRateForVehicleType(vehicleType);

        long durationMinutes = Math.max(
                0,
                Duration.between(entryTime, exitTime).toMinutes()
        );

        // Include partial minutes so even a very short positive stay
        // is charged for at least one hour.
        if (Duration.between(entryTime, exitTime).getSeconds() > 0
                && Duration.between(entryTime, exitTime).getSeconds() % 60 != 0) {
            durationMinutes++;
        }

        long billableHours = Math.max(
                1,
                (long) Math.ceil(durationMinutes / 60.0)
        );

        BigDecimal totalFee = rate.getHourlyRate()
                .multiply(BigDecimal.valueOf(billableHours))
                .setScale(2, RoundingMode.HALF_UP);

        String explanation = String.format(
                "Parked for %d mins. Billed for %d hour(s) @ ₹%.2f/hr = Total ₹%.2f",
                durationMinutes,
                billableHours,
                rate.getHourlyRate(),
                totalFee
        );

        return new CalculationResult(
                durationMinutes,
                totalFee,
                rate,
                explanation
        );
    }

    public static class CalculationResult {
        private final long durationMinutes;
        private final BigDecimal totalFee;
        private final ParkingRate rate;
        private final String explanation;

        public CalculationResult(
                long durationMinutes,
                BigDecimal totalFee,
                ParkingRate rate,
                String explanation) {
            this.durationMinutes = durationMinutes;
            this.totalFee = totalFee;
            this.rate = rate;
            this.explanation = explanation;
        }

        public long getDurationMinutes() {
            return durationMinutes;
        }

        public BigDecimal getTotalFee() {
            return totalFee;
        }

        public ParkingRate getRate() {
            return rate;
        }

        public String getExplanation() {
            return explanation;
        }
    }
}