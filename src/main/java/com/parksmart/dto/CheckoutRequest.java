package com.parksmart.dto;

import com.parksmart.model.PaymentMethod;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CheckoutRequest {

    @NotBlank(message = "License plate number is required")
    private String licensePlate;

    @NotNull(message = "Payment method is required (CASH, UPI, CARD)")
    private PaymentMethod paymentMethod;

    public CheckoutRequest() {}

    public String getLicensePlate() {
        return licensePlate != null ? licensePlate.trim().toUpperCase().replaceAll("\\s+", "") : null;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
