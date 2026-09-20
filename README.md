# ParkSmart - Smart Parking Management System

An enterprise-grade, full-stack Smart Parking Management System built with **Java Spring Boot 3**, **Spring Data JPA**, **MySQL 8.0+**, and a modern **HTML5/CSS3/JavaScript** frontend dashboard.

---

## Key Features

1. **Interactive Multi-Floor Parking Grid**:
   - Multi-floor visualization: **Basement 1**, **Ground Floor**, and **First Floor**.
   - Real-time status indicators: **Green** for AVAILABLE, **Red** for OCCUPIED.
   - Slot details: Bay ID (e.g., `G-ZA-01`), vehicle type compatibility (`🚗 4-Wheeler` vs `🏍️ 2-Wheeler`), and parked license plate.
   - Live auto-refresh polling with a 5-second countdown timer and manual sync toggle.
   - Floor filtering.

2. **Vehicle Check-In & Automatic Bay Allocation**:
   - Input: License plate number (alphanumeric validation), vehicle type, owner name, mobile number, preferred floor & zone.
   - Intelligent allocation engine: Matches compatible slot with preference fallback.
   - Concurrency & double-allocation prevention using atomic database-level locks.
   - Instant Digital **QR Parking Pass** with session code, vehicle plate, allocated bay, and timestamp.
   - Built-in printable pass modal.

3. **Vehicle Checkout & Dynamic Fee Calculation**:
   - Quick lookup by vehicle license plate number.
   - Displays check-in time, current departure time, and elapsed duration.
   - Real-time billing breakdown: Grace period check (15 mins free), base hour fee, and hourly pro-ration.
   - Payment method selection: **UPI / QR**, **Cash**, and **Credit/Debit Card**.
   - Generates official **Settlement Tax Receipt** with unique transaction reference.
   - Automatically frees the parking slot on the live grid upon payment confirmation.

4. **Configurable Dynamic Pricing Rules**:
   - Separate hourly tariffs for 2-Wheelers and 4-Wheelers.
   - Configurable base hours, base rate, hourly rate, and grace period minutes.
   - Edit tariffs directly from the UI without restarting the backend.

5. **Admin Analytics Dashboard**:
   - Real-time KPI cards: Current occupancy count, capacity utilization %, available bays, today's revenue, and vehicle turnover rate.
   - Peak parking hours hourly bar chart.
   - Session audit history log with pagination.
   - Date range filtering.

---

## Technology Stack

- **Backend**: Java 17+ / Java 26, Spring Boot 3.3.4 (REST API, Web, Validation, Data JPA)
- **Database**: MySQL 8.0+ / 26.7 (with automatic H2 in-memory profile fallback)
- **Frontend**: HTML5, Vanilla CSS3 (Modern Glassmorphic Dark Luxury Design), Modern JavaScript ES6+
- **QR Code Engine**: ZXing (Zebra Crossing) 3.5.3
- **Testing**: JUnit 5, Mockito, Postman Collection
- **Diagrams**: Draw.io XML Architecture Diagram

---

## Directory Structure

```
ParkSmart/
├── src/
│   ├── main/
│   │   ├── java/com/parksmart/
│   │   │   ├── controller/          # REST Controllers
│   │   │   │   ├── AnalyticsController.java
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ParkingCheckoutController.java
│   │   │   │   ├── ParkingEntryController.java
│   │   │   │   ├── ParkingGridController.java
│   │   │   │   └── ParkingRateController.java
│   │   │   ├── dto/                 # Data Transfer Objects & Payloads
│   │   │   ├── exception/           # Custom Domain Exceptions
│   │   │   ├── model/               # JPA Entities (Floor, Zone, Slot, Vehicle, Session, Payment, Rate)
│   │   │   ├── repository/          # Spring Data JPA Repositories
│   │   │   ├── service/             # Business Logic & Allocation Engine
│   │   │   └── ParkSmartApplication.java
│   │   └── resources/
│   │       ├── application.properties    # MySQL Configuration
│   │       ├── application-h2.properties # Offline In-Memory Fallback
│   │       ├── data.sql                  # Initial Seed Data (Floors, Zones, 30+ Slots, Rates)
│   │       └── static/                   # Frontend SPA
│   │           ├── css/styles.css
│   │           ├── js/app.js
│   │           └── index.html
│   └── test/java/com/parksmart/     # Unit & Integration Tests
├── pom.xml                          # Maven Configuration
├── ParkSmart_Architecture.drawio    # Draw.io Architecture Diagram
├── ParkSmart_Postman_Collection.json# Complete API Test Suite
├── TESTING_CHECKLIST.md             # Testing & QA Verification Guide
├── PRESENTATION_OUTLINE.md          # College Viva & Presentation Guide
└── README.md
```

---

## Database Configuration

The application is configured to connect to MySQL on `localhost:3306`.

1. **MySQL Configuration (`src/main/resources/application.properties`)**:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/parksmart_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
   spring.datasource.username=root
   spring.datasource.password=${MYSQL_PASSWORD:root}
   ```
   *If your MySQL root user has a password other than `root`, set the `MYSQL_PASSWORD` environment variable or edit `application.properties` directly.*

2. **In-Memory H2 Fallback (Zero Setup)**:
   If you want to run the application immediately without starting or configuring MySQL:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=h2
   ```

---

## How to Build and Run the Application

### Option A: Using Maven (if `mvn` is on your PATH)
```bash
# 1. Clean and compile
mvn clean compile

# 2. Run unit tests
mvn test

# 3. Start the application
mvn spring-boot:run
```

### Option B: Using the Included Portable Runner (`run.ps1`)
We provide a standalone runner script `run.ps1` that will automatically launch the Spring Boot server.

Once running:
- Open your browser and navigate to: **`http://localhost:8080`**
- The full interactive dashboard will appear immediately!

---

## REST API Endpoints Overview

| Method | Endpoint | Description |
|---|---|---|
| `GET` | `/api/floors` | Get all floors, zones, and parking bays |
| `GET` | `/api/grid/slots?floorId=...` | Get slots filtered by floor |
| `POST`| `/api/parking/entry` | Register vehicle check-in & allocate bay |
| `GET` | `/api/parking/active/{plate}` | Find active parking session and fee preview |
| `POST`| `/api/parking/checkout` | Settle payment and free the bay |
| `GET` | `/api/parking/history` | View recent parking sessions log |
| `GET` | `/api/analytics/summary` | Real-time occupancy, revenue, peak hours |
| `GET` | `/api/rates` | View active tariffs |
| `PUT` | `/api/rates/{id}` | Update tariff rules dynamically |

---

## Testing with Postman

1. Open Postman.
2. Click **Import** in the top-left corner.
3. Select `ParkSmart_Postman_Collection.json` from the project root.
4. Run requests to test check-in, checkout, and analytics endpoints.
