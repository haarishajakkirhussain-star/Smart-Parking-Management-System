
package com.parksmart;

import com.parksmart.model.ParkingRate;
import com.parksmart.model.VehicleType;
import com.parksmart.repository.ParkingRateRepository;
import com.parksmart.service.PricingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class PricingServiceTest {

    private ParkingRateRepository rateRepository;
    private PricingService pricingService;

    @BeforeEach
    void setUp() {
        rateRepository = Mockito.mock(ParkingRateRepository.class);
        pricingService = new PricingService(rateRepository);

        ParkingRate twoWheelerRate = new ParkingRate(
                VehicleType.TWO_WHEELER,
                new BigDecimal("20.00"),
                1,
                new BigDecimal("20.00"),
                15
        );

        ParkingRate fourWheelerRate = new ParkingRate(
                VehicleType.FOUR_WHEELER,
                new BigDecimal("40.00"),
                1,
                new BigDecimal("40.00"),
                15
        );

        when(rateRepository.findByVehicleTypeAndIsActiveTrue(
                VehicleType.TWO_WHEELER
        )).thenReturn(Optional.of(twoWheelerRate));

        when(rateRepository.findByVehicleTypeAndIsActiveTrue(
                VehicleType.FOUR_WHEELER
        )).thenReturn(Optional.of(fourWheelerRate));
    }

    @Test
    @DisplayName("4 minutes should be charged as 1 hour")
    void testFourMinutesCharge() {
        LocalDateTime entryTime = LocalDateTime.now().minusMinutes(4);
        LocalDateTime exitTime = LocalDateTime.now();

        PricingService.CalculationResult result =
                pricingService.calculateFee(
                        VehicleType.FOUR_WHEELER,
                        entryTime,
                        exitTime
                );

        assertEquals(
                new BigDecimal("40.00"),
                result.getTotalFee()
        );
    }

    @Test
    @DisplayName("60 minutes should be charged as 1 hour")
    void testOneHourCharge() {
        LocalDateTime entryTime = LocalDateTime.now().minusMinutes(60);
        LocalDateTime exitTime = LocalDateTime.now();

        PricingService.CalculationResult result =
                pricingService.calculateFee(
                        VehicleType.FOUR_WHEELER,
                        entryTime,
                        exitTime
                );

        assertEquals(
                new BigDecimal("40.00"),
                result.getTotalFee()
        );
    }

    @Test
    @DisplayName("61 minutes should be charged as 2 hours")
    void testSixtyOneMinutesCharge() {
        LocalDateTime entryTime = LocalDateTime.now().minusMinutes(61);
        LocalDateTime exitTime = LocalDateTime.now();

        PricingService.CalculationResult result =
                pricingService.calculateFee(
                        VehicleType.FOUR_WHEELER,
                        entryTime,
                        exitTime
                );

        assertEquals(
                new BigDecimal("80.00"),
                result.getTotalFee()
        );
    }

    @Test
    @DisplayName("2 hours should be charged as 2 hours")
    void testTwoHourCharge() {
        LocalDateTime entryTime = LocalDateTime.now().minusHours(2);
        LocalDateTime exitTime = LocalDateTime.now();

        PricingService.CalculationResult result =
                pricingService.calculateFee(
                        VehicleType.FOUR_WHEELER,
                        entryTime,
                        exitTime
                );

        assertEquals(
                new BigDecimal("80.00"),
                result.getTotalFee()
        );
    }

    @Test
    @DisplayName("2-wheeler should be charged ₹20 for 4 minutes")
    void testTwoWheelerMinimumCharge() {
        LocalDateTime entryTime = LocalDateTime.now().minusMinutes(4);
        LocalDateTime exitTime = LocalDateTime.now();

        PricingService.CalculationResult result =
                pricingService.calculateFee(
                        VehicleType.TWO_WHEELER,
                        entryTime,
                        exitTime
                );

        assertEquals(
                new BigDecimal("20.00"),
                result.getTotalFee()
        );
    }
}