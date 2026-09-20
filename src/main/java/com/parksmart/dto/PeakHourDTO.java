package com.parksmart.dto;

public class PeakHourDTO {

    private int hour;
    private String hourLabel;
    private long vehicleCount;

    public PeakHourDTO() {}

    public PeakHourDTO(int hour, String hourLabel, long vehicleCount) {
        this.hour = hour;
        this.hourLabel = hourLabel;
        this.vehicleCount = vehicleCount;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public String getHourLabel() {
        return hourLabel;
    }

    public void setHourLabel(String hourLabel) {
        this.hourLabel = hourLabel;
    }

    public long getVehicleCount() {
        return vehicleCount;
    }

    public void setVehicleCount(long vehicleCount) {
        this.vehicleCount = vehicleCount;
    }
}
