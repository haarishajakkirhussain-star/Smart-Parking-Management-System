package com.parksmart.repository;

import com.parksmart.model.Payment;
import com.parksmart.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findBySessionId(Long sessionId);

    List<Payment> findByPaymentStatus(PaymentStatus paymentStatus);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p " +
           "WHERE p.paymentStatus = 'PAID' AND p.paymentTime >= :startTime AND p.paymentTime < :endTime")
    BigDecimal calculateTotalRevenueBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT p FROM Payment p WHERE p.paymentTime >= :startTime AND p.paymentTime < :endTime ORDER BY p.paymentTime DESC")
    List<Payment> findPaymentsBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);
}
