# ParkSmart - Project Presentation & Viva Outline

Use this structured guide for your college project viva, seminar, or final year demonstration.

---

## Slide 1: Title & Introduction
- **Project Title**: ParkSmart - Intelligent Smart Parking Management System
- **Domain**: Web Application, Internet of Things (IoT) simulation, Enterprise Software Engineering
- **Presenter**: [Your Name]
- **Key Objective**: Solving urban congestion and parking inefficiency through automated bay allocation, dynamic tariff pricing, and contactless QR parking passes.

---

## Slide 2: Problem Statement & Motivation
- Urban vehicles waste an average of 15–20 minutes searching for vacant parking slots, resulting in fuel wastage, traffic congestion, and frustration.
- Traditional parking lots rely on manual ticketing, leading to:
  - Human error and cash mismanagement
  - Lack of real-time multi-floor visibility
  - Double allocation conflicts
  - Inflexible, static billing structures

---

## Slide 3: Proposed Solution & Core Features
1. **Interactive Multi-Floor Grid**: Visual map across Basement, Ground, and Upper floors with real-time green (available) and red (occupied) slots.
2. **Automated Algorithmic Allocation**: Instant assignment of compatible slots with race-condition prevention and preference matching.
3. **Contactless Digital QR Pass**: Real-time pass with embedded session verification.
4. **Dynamic Tariff Engine**: Configurable hourly billing with grace periods and vehicle classification (2-Wheelers vs 4-Wheelers).
5. **Admin Business Intelligence**: Live occupancy %, daily revenue ledger, turnover metrics, and peak hour traffic distribution.

---

## Slide 4: Technology Stack & Architectural Design
- **Architecture**: Three-Tier Client-Server Architecture (SPA + REST API + Relational DB)
- **Backend**: Java 17+ / 26, Spring Boot 3.3 REST API, Spring Data JPA, Hibernate ORM
- **Database**: MySQL 8.0+ Enterprise with relational constraints, indexes, and atomic updates
- **Frontend**: HTML5, Vanilla CSS3 (Glassmorphic Luxury Dark Design System), Modern JavaScript ES6+
- **Security & Integrity**: Jakarta Bean Validation, `@Transactional` boundaries, Optimistic/Pessimistic locking, UUID tokens

---

## Slide 5: Database Schema (Entity-Relationship)
- `ParkingFloor`: Physical levels (Basement 1, Ground, Floor 1)
- `ParkingZone`: Specialized wings per floor (Zone A, Zone B)
- `ParkingSlot`: Individual bays with vehicle compatibility and occupancy state
- `Vehicle`: Unique license plate repository and vehicle details
- `ParkingSession`: Active/completed lifecycle, entry/exit timestamps, duration, and fee
- `Payment`: Audit trail with transaction reference, method (UPI/Cash/Card), and status
- `ParkingRate`: Dynamic configuration of base and hourly tariffs

---

## Slide 6: Key Algorithms & Concurrency Handling
- **Double Allocation Prevention**:
  ```sql
  UPDATE parking_slots SET status = 'OCCUPIED', is_occupied = true, current_vehicle_plate = ?
  WHERE id = ? AND status = 'AVAILABLE';
  ```
  Guarantees that two concurrent vehicles arriving at the same millisecond can never receive the same parking bay.
- **Dynamic Pricing Formula**:
  $$\text{Fee} = \begin{cases} 
  0 & \text{if } t \le t_{\text{grace}} \\ 
  \text{BaseRate} & \text{if } t \le t_{\text{base}} \\ 
  \text{BaseRate} + \lceil\frac{t - t_{\text{base}}}{60}\rceil \times \text{HourlyRate} & \text{otherwise} 
  \end{cases}$$

---

## Slide 7: Live Demonstration Walkthrough Script
1. **Grid Overview**: Show multi-floor layout and color indicators.
2. **Vehicle Check-In**: Register a 4-Wheeler (`MH12AB1234`), watch the allocated bay instantly illuminate red on the live grid, and showcase the generated QR Parking Pass.
3. **Double Allocation Test**: Attempt checking in the same vehicle again; show the system blocking duplicate sessions.
4. **Vehicle Checkout**: Search for `MH12AB1234`, demonstrate transparent fee breakdown, select UPI payment, and complete checkout.
5. **Real-Time Settlement**: Observe the bay turning green again, review the printable official tax invoice, and verify revenue and peak hour updates in the Admin Analytics tab.

---

## Slide 8: Viva Q&A - Common Questions & Answers

**Q1: How does your application prevent double allocation if two vehicles arrive simultaneously?**
> *Answer*: We employ atomic conditional updates at the database level (`UPDATE ... WHERE status = 'AVAILABLE'`). If multiple requests target the same slot simultaneously, exactly one row update succeeds (`rowsUpdated = 1`), while subsequent conflicting threads fail and automatically fall back to the next available slot.

**Q2: Why use Spring Boot 3 with JPA instead of standard JDBC?**
> *Answer*: Spring Boot provides production-ready features (embedded web server, transaction management, dependency injection) while Spring Data JPA abstracts boilerplate queries, provides automated schema generation, type safety, and relationship mappings.

**Q3: How is the QR code generated and verified?**
> *Answer*: We utilize the ZXing (Zebra Crossing) library on the backend to encode a JSON payload containing session ID, vehicle plate, bay code, and timestamp into a Base64 data URI rendered directly in HTML without external API dependencies.

**Q4: Can the parking tariffs be changed without restarting the server?**
> *Answer*: Yes, tariffs are stored in the `parking_rates` table in MySQL and managed via REST endpoints (`/api/rates`), allowing real-time adjustments that take effect immediately.

---

## Slide 9: Conclusion & Future Scope
- **Conclusion**: ParkSmart demonstrates a complete, reliable, and user-centric solution for modern automated parking infrastructure.
- **Future Roadmap**:
  - Automated Number Plate Recognition (ANPR) via computer vision cameras.
  - IoT sensor integration (ultrasonic / magnetometer sensors).
  - Integration with FASTag / payment gateways for automated toll deduction.
