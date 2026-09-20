package com.parksmart.dto;

import java.math.BigDecimal;
import java.util.List;

public class AnalyticsSummaryDTO {

    private long totalCapacity;
    private long occupiedSlots;
    private long availableSlots;
    private double occupancyPercentage;

    private long twoWheelersParked;
    private long fourWheelersParked;

    private BigDecimal todayRevenue;
    private long totalSessionsToday;
    private double vehicleTurnoverRate; // sessions today / total capacity

    private List<PeakHourDTO> peakHours;

    public AnalyticsSummaryDTO() {}

    public long getTotalCapacity() {
        return totalCapacity;
    }

    public void setTotalCapacity(long totalCapacity) {
        this.totalCapacity = totalCapacity;
    }

    public long getOccupiedSlots() {
        return occupiedSlots;
    }

    public void setOccupiedSlots(long occupiedSlots) {
        this.occupiedSlots = occupiedSlots;
    }

    public long getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(long availableSlots) {
        this.availableSlots = availableSlots;
    }

    public double getOccupancyPercentage() {
        return occupancyPercentage;
    }

    public void setOccupancyPercentage(double occupancyPercentage) {
        this.occupancyPercentage = occupancyPercentage;
    }

    public long getTwoWheelersParked() {
        return twoWheelersParked;
    }

    public void setTwoWheelersParked(long twoWheelersParked) {
        this.twoWheelersParked = twoWheelersParked;
    }

    public long getFourWheelersParked() {
        return fourWheelersParked;
    }

    public void setFourWheelersParked(long fourWheelersParked) {
        this.fourWheelersParked = fourWheelersParked;
    }

    public BigDecimal getTodayRevenue() {
        return todayRevenue;
    }

    public void setTodayRevenue(BigDecimal todayRevenue) {
        this.todayRevenue = todayRevenue;
    }

    public long getTotalSessionsToday() {
        return totalSessionsToday;
    }

    public void setTotalSessionsToday(long totalSessionsToday) {
        this.totalSessionsToday = totalSessionsToday;
    }

    public double getVehicleTurnoverRate() {
        return vehicleTurnoverRate;
    }

    public void setVehicleTurnoverRate(double vehicleTurnoverRate) {
        this.vehicleTurnoverRate = vehicleTurnoverRate;
    }

    public List<PeakHourDTO> getPeakHours() {
        return peakHours;
    }

    public void setPeakHours(List<PeakHourDTO> peakHours) {
        this.peakHours = peakHours;
    }
}
