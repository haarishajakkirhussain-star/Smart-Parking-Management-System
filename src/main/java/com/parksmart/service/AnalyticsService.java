package com.parksmart.service;

import com.parksmart.dto.AnalyticsSummaryDTO;
import com.parksmart.dto.PeakHourDTO;
import com.parksmart.model.SlotStatus;
import com.parksmart.model.VehicleType;
import com.parksmart.repository.ParkingSessionRepository;
import com.parksmart.repository.ParkingSlotRepository;
import com.parksmart.repository.PaymentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AnalyticsService {

    private final ParkingSlotRepository slotRepository;
    private final ParkingSessionRepository sessionRepository;
    private final PaymentRepository paymentRepository;

    public AnalyticsService(ParkingSlotRepository slotRepository,
                            ParkingSessionRepository sessionRepository,
                            PaymentRepository paymentRepository) {
        this.slotRepository = slotRepository;
        this.sessionRepository = sessionRepository;
        this.paymentRepository = paymentRepository;
    }

    @Transactional(readOnly = true)
    public AnalyticsSummaryDTO getSummary(LocalDate targetDate) {
        LocalDate date = (targetDate != null) ? targetDate : LocalDate.now();
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

        long totalCapacity = slotRepository.count();
        long occupiedSlots = slotRepository.countByStatus(SlotStatus.OCCUPIED);
        long availableSlots = Math.max(0, totalCapacity - occupiedSlots);

        double occupancyPercentage = totalCapacity > 0
                ? ((double) occupiedSlots / totalCapacity) * 100.0
                : 0.0;

        long twoWheelersParked = slotRepository.countByVehicleTypeAndStatus(VehicleType.TWO_WHEELER, SlotStatus.OCCUPIED);
        long fourWheelersParked = slotRepository.countByVehicleTypeAndStatus(VehicleType.FOUR_WHEELER, SlotStatus.OCCUPIED);

        BigDecimal todayRevenue = paymentRepository.calculateTotalRevenueBetween(dayStart, dayEnd);
        if (todayRevenue == null) {
            todayRevenue = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }

        long totalSessionsToday = sessionRepository.countSessionsEnteredBetween(dayStart, dayEnd);

        double turnoverRate = totalCapacity > 0
                ? (double) totalSessionsToday / totalCapacity
                : 0.0;

        List<PeakHourDTO> peakHours = getPeakHoursForDate(dayStart, dayEnd);

        AnalyticsSummaryDTO summary = new AnalyticsSummaryDTO();
        summary.setTotalCapacity(totalCapacity);
        summary.setOccupiedSlots(occupiedSlots);
        summary.setAvailableSlots(availableSlots);
        summary.setOccupancyPercentage(Math.round(occupancyPercentage * 10.0) / 10.0);
        summary.setTwoWheelersParked(twoWheelersParked);
        summary.setFourWheelersParked(fourWheelersParked);
        summary.setTodayRevenue(todayRevenue);
        summary.setTotalSessionsToday(totalSessionsToday);
        summary.setVehicleTurnoverRate(Math.round(turnoverRate * 100.0) / 100.0);
        summary.setPeakHours(peakHours);

        return summary;
    }

    public List<PeakHourDTO> getPeakHoursForDate(LocalDateTime start, LocalDateTime end) {
        List<Object[]> rawData = sessionRepository.getPeakHoursAnalytics(start, end);
        Map<Integer, Long> hourCounts = new HashMap<>();

        for (Object[] row : rawData) {
            if (row != null && row.length >= 2 && row[0] != null && row[1] != null) {
                int hour = ((Number) row[0]).intValue();
                long count = ((Number) row[1]).longValue();
                hourCounts.put(hour, count);
            }
        }

        List<PeakHourDTO> result = new ArrayList<>();
        // Generate common operational hours (e.g., 8:00 AM to 9:00 PM)
        for (int h = 8; h <= 21; h++) {
            long count = hourCounts.getOrDefault(h, 0L);
            String label = formatHourLabel(h);
            result.add(new PeakHourDTO(h, label, count));
        }

        return result;
    }

    private String formatHourLabel(int hour) {
        if (hour == 0) return "12 AM";
        if (hour < 12) return hour + " AM";
        if (hour == 12) return "12 PM";
        return (hour - 12) + " PM";
    }
}
