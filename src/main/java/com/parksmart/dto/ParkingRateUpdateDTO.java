package com.parksmart.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ParkingRateUpdateDTO {

    @NotNull(message = "Hourly rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Hourly rate must be greater than 0")
    private BigDecimal hourlyRate;

    @NotNull(message = "Base rate is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Base rate must be 0 or more")
    private BigDecimal baseRate;

    @NotNull(message = "Base hours is required")
    @Min(value = 1, message = "Base hours must be at least 1")
    private Integer baseHours;

    @NotNull(message = "Grace period minutes is required")
    @Min(value = 0, message = "Grace period cannot be negative")
    private Integer gracePeriodMinutes;

    public ParkingRateUpdateDTO() {}

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public BigDecimal getBaseRate() {
        return baseRate;
    }

    public void setBaseRate(BigDecimal baseRate) {
        this.baseRate = baseRate;
    }

    public Integer getBaseHours() {
        return baseHours;
    }

    public void setBaseHours(Integer baseHours) {
        this.baseHours = baseHours;
    }

    public Integer getGracePeriodMinutes() {
        return gracePeriodMinutes;
    }

    public void setGracePeriodMinutes(Integer gracePeriodMinutes) {
        this.gracePeriodMinutes = gracePeriodMinutes;
    }
}
