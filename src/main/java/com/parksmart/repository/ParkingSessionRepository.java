package com.parksmart.repository;

import com.parksmart.model.ParkingSession;
import com.parksmart.model.SessionStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParkingSessionRepository extends JpaRepository<ParkingSession, Long> {

    Optional<ParkingSession> findBySessionCode(String sessionCode);

    @Query("SELECT s FROM ParkingSession s WHERE UPPER(s.vehicle.licensePlate) = UPPER(:plate) AND s.status = 'ACTIVE'")
    Optional<ParkingSession> findActiveSessionByLicensePlate(@Param("plate") String plate);

    @Query("SELECT s FROM ParkingSession s WHERE UPPER(s.vehicle.licensePlate) = UPPER(:plate) ORDER BY s.entryTime DESC")
    List<ParkingSession> findHistoryByLicensePlate(@Param("plate") String plate);

    long countByStatus(SessionStatus status);

    @Query("SELECT COUNT(s) FROM ParkingSession s WHERE s.entryTime >= :startTime AND s.entryTime < :endTime")
    long countSessionsEnteredBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    @Query("SELECT s FROM ParkingSession s WHERE s.entryTime >= :startTime AND s.entryTime < :endTime ORDER BY s.entryTime DESC")
    List<ParkingSession> findSessionsBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    // Group entries by hour for peak hour analytics
    @Query("SELECT HOUR(s.entryTime) as hourOfDay, COUNT(s) as sessionCount " +
           "FROM ParkingSession s " +
           "WHERE s.entryTime >= :startDate AND s.entryTime < :endDate " +
           "GROUP BY HOUR(s.entryTime) " +
           "ORDER BY HOUR(s.entryTime) ASC")
    List<Object[]> getPeakHoursAnalytics(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    Page<ParkingSession> findAllByOrderByEntryTimeDesc(Pageable pageable);
}
