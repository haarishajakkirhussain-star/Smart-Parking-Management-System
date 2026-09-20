# ParkSmart - Comprehensive Testing Checklist

This checklist provides a structured guide to verify every component of the Smart Parking Management System.

---

## 1. Environment & Setup Testing
- [ ] **Java Version**: Ensure JDK 17+ / JDK 26 is installed (`java -version`).
- [ ] **MySQL Service**: Ensure MySQL 8.0+ service is running (`Get-Service *mysql*`).
- [ ] **Database Connection**: Database `parksmart_db` created with seed data from `data.sql`.
- [ ] **Web Application Access**: Open `http://localhost:8080` in web browser.

---

## 2. Interactive Parking Grid Testing
- [ ] **Multi-Floor Rendering**: Verify Basement 1, Ground Floor, and First Floor appear.
- [ ] **Status Color Scheme**:
  - Green border & badge for `AVAILABLE` slots.
  - Red border & badge for `OCCUPIED` slots.
- [ ] **Vehicle Type Badges**:
  - `🚗` icon displayed for 4-Wheeler bays.
  - `🏍️` icon displayed for 2-Wheeler bays.
- [ ] **Vehicle Plate Display**: Occupied bays show the parked vehicle license plate.
- [ ] **Auto-Refresh**: Verify the 5-second countdown timer triggers real-time updates.
- [ ] **Floor Filter**: Clicking "Ground Floor" filters the view to only Ground Floor bays.

---

## 3. Vehicle Entry & Allocation Testing
- [ ] **Valid 4-Wheeler Entry**:
  - Enter Plate: `MH12AB1234`, Type: `4-Wheeler`.
  - Expectation: Assigned an available 4-Wheeler bay (e.g. `B1-ZA-01`).
  - Grid bay turns RED with license plate `MH12AB1234`.
  - Digital QR Parking Pass rendered on screen with QR code, session ID, bay ID, and timestamp.
- [ ] **Valid 2-Wheeler Entry**:
  - Enter Plate: `KA01CD5678`, Type: `2-Wheeler`.
  - Expectation: Assigned a 2-Wheeler bay (e.g. `B1-ZB-01`).
  - Grid bay turns RED with license plate `KA01CD5678`.
- [ ] **Floor Preference Test**:
  - Select preferred floor "Ground Floor".
  - Expectation: Allocates bay `G-ZA-01` or `G-ZB-01`.
- [ ] **Duplicate Active Session Prevention**:
  - Try submitting `MH12AB1234` again while still active.
  - Expectation: System returns error: *"Vehicle MH12AB1234 already has an active parking session in bay..."*.
- [ ] **Invalid License Plate Validation**:
  - Submit empty plate or invalid characters.
  - Expectation: Validation error displayed, form blocked.
- [ ] **Capacity Saturation Test**:
  - When all bays of a type are occupied, system cleanly returns: *"All compatible parking bays for ... are currently occupied."*

---

## 4. Vehicle Checkout & Dynamic Fee Calculation Testing
- [ ] **License Plate Search**:
  - Enter `MH12AB1234` in search box.
  - Expectation: Displays active session, check-in time, current time, duration, and calculated fee.
- [ ] **Grace Period Test**:
  - If checkout is requested within 15 minutes of check-in.
  - Expectation: Fee is `₹0.00` with explanation *"Parked for X mins (within 15 mins grace period)"*.
- [ ] **Base Fee Calculation**:
  - Parked between 15 mins and 60 mins.
  - Expectation: Charged base fee (₹40 for 4-Wheeler, ₹20 for 2-Wheeler).
- [ ] **Multi-Hour Tariff Calculation**:
  - Verify pro-rated ceiling for each additional hour.
- [ ] **Payment Confirmation & Bay Release**:
  - Select UPI / Cash / Card and click "Confirm Payment & Free Bay".
  - Expectation:
    - Session status updated to `COMPLETED`.
    - Payment record generated with unique transaction reference.
    - Bay status turns GREEN (`AVAILABLE`) on the interactive grid.
    - Official tax receipt modal generated with Print button.
- [ ] **Non-Existent Vehicle Search**:
  - Search for a license plate not parked in the system.
  - Expectation: Shows clean error: *"No active parking session found for license plate: ..."*.

---

## 5. Admin Analytics & Dynamic Pricing Testing
- [ ] **Live Occupancy Metrics**:
  - Total Capacity, Occupied count, and Available count update accurately after each check-in and check-out.
  - Occupancy percentage meter updates in header and dashboard.
- [ ] **Today's Revenue**:
  - Revenue tally increases by exact fee collected upon each confirmed payment.
- [ ] **Turnover Rate**:
  - Ratio of total daily sessions to capacity computes correctly.
- [ ] **Peak Hours Histogram**:
  - Hourly bars reflect vehicle entry timestamps.
- [ ] **Dynamic Tariff Configuration**:
  - Change 4-Wheeler hourly rate from ₹40 to ₹50 and click "Update Tariff".
  - Expectation: Next check-in/checkout uses updated ₹50/hr rate without application restart.
- [ ] **Session Audit Log**:
  - Recent sessions table lists vehicle plate, bay code, entry/exit timestamps, fee, and payment status.
