-- =========================================================
-- ParkSmart Seed Data: Floors, Zones, Slots & Default Rates
-- =========================================================

-- Insert Floors
INSERT INTO parking_floors (id, floor_number, floor_name) VALUES 
(1, -1, 'Basement 1'),
(2, 0, 'Ground Floor'),
(3, 1, 'First Floor')
ON DUPLICATE KEY UPDATE floor_name = VALUES(floor_name);

-- Insert Zones
INSERT INTO parking_zones (id, zone_code, zone_name, floor_id) VALUES
(1, 'A', 'Section A - 4 Wheelers', 1),
(2, 'B', 'Section B - 2 Wheelers', 1),
(3, 'A', 'Main Plaza - 4 Wheelers', 2),
(4, 'B', 'Express Bay - 2 Wheelers', 2),
(5, 'A', 'Elevated Deck - 4 Wheelers', 3),
(6, 'B', 'Rooftop Bay - 2 Wheelers', 3)
ON DUPLICATE KEY UPDATE zone_name = VALUES(zone_name);

-- Insert Slots for Floor 1 (Basement 1) - Zone A (4-Wheelers)
INSERT INTO parking_slots (id, slot_number, slot_code, vehicle_type, status, is_occupied, zone_id, version) VALUES
(1, 1, 'B1-ZA-01', 'FOUR_WHEELER', 'AVAILABLE', 0, 1, 0),
(2, 2, 'B1-ZA-02', 'FOUR_WHEELER', 'AVAILABLE', 0, 1, 0),
(3, 3, 'B1-ZA-03', 'FOUR_WHEELER', 'AVAILABLE', 0, 1, 0),
(4, 4, 'B1-ZA-04', 'FOUR_WHEELER', 'AVAILABLE', 0, 1, 0),
(5, 5, 'B1-ZA-05', 'FOUR_WHEELER', 'AVAILABLE', 0, 1, 0)
ON DUPLICATE KEY UPDATE slot_code = VALUES(slot_code);

-- Insert Slots for Floor 1 (Basement 1) - Zone B (2-Wheelers)
INSERT INTO parking_slots (id, slot_number, slot_code, vehicle_type, status, is_occupied, zone_id, version) VALUES
(6, 1, 'B1-ZB-01', 'TWO_WHEELER', 'AVAILABLE', 0, 2, 0),
(7, 2, 'B1-ZB-02', 'TWO_WHEELER', 'AVAILABLE', 0, 2, 0),
(8, 3, 'B1-ZB-03', 'TWO_WHEELER', 'AVAILABLE', 0, 2, 0),
(9, 4, 'B1-ZB-04', 'TWO_WHEELER', 'AVAILABLE', 0, 2, 0),
(10, 5, 'B1-ZB-05', 'TWO_WHEELER', 'AVAILABLE', 0, 2, 0)
ON DUPLICATE KEY UPDATE slot_code = VALUES(slot_code);

-- Insert Slots for Floor 2 (Ground Floor) - Zone A (4-Wheelers)
INSERT INTO parking_slots (id, slot_number, slot_code, vehicle_type, status, is_occupied, zone_id, version) VALUES
(11, 1, 'G-ZA-01', 'FOUR_WHEELER', 'AVAILABLE', 0, 3, 0),
(12, 2, 'G-ZA-02', 'FOUR_WHEELER', 'AVAILABLE', 0, 3, 0),
(13, 3, 'G-ZA-03', 'FOUR_WHEELER', 'AVAILABLE', 0, 3, 0),
(14, 4, 'G-ZA-04', 'FOUR_WHEELER', 'AVAILABLE', 0, 3, 0),
(15, 5, 'G-ZA-05', 'FOUR_WHEELER', 'AVAILABLE', 0, 3, 0),
(16, 6, 'G-ZA-06', 'FOUR_WHEELER', 'AVAILABLE', 0, 3, 0)
ON DUPLICATE KEY UPDATE slot_code = VALUES(slot_code);

-- Insert Slots for Floor 2 (Ground Floor) - Zone B (2-Wheelers)
INSERT INTO parking_slots (id, slot_number, slot_code, vehicle_type, status, is_occupied, zone_id, version) VALUES
(17, 1, 'G-ZB-01', 'TWO_WHEELER', 'AVAILABLE', 0, 4, 0),
(18, 2, 'G-ZB-02', 'TWO_WHEELER', 'AVAILABLE', 0, 4, 0),
(19, 3, 'G-ZB-03', 'TWO_WHEELER', 'AVAILABLE', 0, 4, 0),
(20, 4, 'G-ZB-04', 'TWO_WHEELER', 'AVAILABLE', 0, 4, 0),
(21, 5, 'G-ZB-05', 'TWO_WHEELER', 'AVAILABLE', 0, 4, 0),
(22, 6, 'G-ZB-06', 'TWO_WHEELER', 'AVAILABLE', 0, 4, 0)
ON DUPLICATE KEY UPDATE slot_code = VALUES(slot_code);

-- Insert Slots for Floor 3 (First Floor) - Zone A (4-Wheelers)
INSERT INTO parking_slots (id, slot_number, slot_code, vehicle_type, status, is_occupied, zone_id, version) VALUES
(23, 1, 'F1-ZA-01', 'FOUR_WHEELER', 'AVAILABLE', 0, 5, 0),
(24, 2, 'F1-ZA-02', 'FOUR_WHEELER', 'AVAILABLE', 0, 5, 0),
(25, 3, 'F1-ZA-03', 'FOUR_WHEELER', 'AVAILABLE', 0, 5, 0),
(26, 4, 'F1-ZA-04', 'FOUR_WHEELER', 'AVAILABLE', 0, 5, 0)
ON DUPLICATE KEY UPDATE slot_code = VALUES(slot_code);

-- Insert Slots for Floor 3 (First Floor) - Zone B (2-Wheelers)
INSERT INTO parking_slots (id, slot_number, slot_code, vehicle_type, status, is_occupied, zone_id, version) VALUES
(27, 1, 'F1-ZB-01', 'TWO_WHEELER', 'AVAILABLE', 0, 6, 0),
(28, 2, 'F1-ZB-02', 'TWO_WHEELER', 'AVAILABLE', 0, 6, 0),
(29, 3, 'F1-ZB-03', 'TWO_WHEELER', 'AVAILABLE', 0, 6, 0),
(30, 4, 'F1-ZB-04', 'TWO_WHEELER', 'AVAILABLE', 0, 6, 0)
ON DUPLICATE KEY UPDATE slot_code = VALUES(slot_code);

-- Insert Configurable Parking Rates (₹20/hr for 2-Wheelers, ₹40/hr for 4-Wheelers, 15m grace period)
INSERT INTO parking_rates (id, vehicle_type, hourly_rate, base_hours, base_rate, grace_period_minutes, is_active) VALUES
(1, 'TWO_WHEELER', 20.00, 1, 20.00, 15, 1),
(2, 'FOUR_WHEELER', 40.00, 1, 40.00, 15, 1)
ON DUPLICATE KEY UPDATE hourly_rate = VALUES(hourly_rate), base_rate = VALUES(base_rate);
