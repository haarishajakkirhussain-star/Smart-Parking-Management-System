package com.parksmart.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "parking_sessions", indexes = {
    @Index(name = "idx_session_code", columnList = "session_code", unique = true),
    @Index(name = "idx_session_status", columnList = "status"),
    @Index(name = "idx_session_entry_time", columnList = "entry_time")
})
public class ParkingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "session_code", nullable = false, unique = true, length = 64)
    private String sessionCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "slot_id", nullable = false)
    private ParkingSlot slot;

    @Column(name = "entry_time", nullable = false)
    private LocalDateTime entryTime;

    @Column(name = "exit_time")
    private LocalDateTime exitTime;

    @Column(name = "duration_minutes")
    private Long durationMinutes;

    @Column(name = "total_fee", precision = 10, scale = 2)
    private BigDecimal totalFee;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private SessionStatus status = SessionStatus.ACTIVE;

    @Column(name = "qr_token", length = 100)
    private String qrToken;

    public ParkingSession() {}

    public ParkingSession(Vehicle vehicle, ParkingSlot slot) {
        this.sessionCode = "PS-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        this.vehicle = vehicle;
        this.slot = slot;
        this.entryTime = LocalDateTime.now();
        this.status = SessionStatus.ACTIVE;
        this.qrToken = UUID.randomUUID().toString();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSessionCode() {
        return sessionCode;
    }

    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public void setVehicle(Vehicle vehicle) {
        this.vehicle = vehicle;
    }

    public ParkingSlot getSlot() {
        return slot;
    }

    public void setSlot(ParkingSlot slot) {
        this.slot = slot;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public Long getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Long durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public BigDecimal getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(BigDecimal totalFee) {
        this.totalFee = totalFee;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public String getQrToken() {
        return qrToken;
    }

    public void setQrToken(String qrToken) {
        this.qrToken = qrToken;
    }
}
