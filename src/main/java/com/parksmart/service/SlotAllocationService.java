package com.parksmart.service;

import com.parksmart.exception.SlotNotAvailableException;
import com.parksmart.model.ParkingSlot;
import com.parksmart.model.SlotStatus;
import com.parksmart.model.VehicleType;
import com.parksmart.repository.ParkingSlotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SlotAllocationService {

    private final ParkingSlotRepository slotRepository;

    public SlotAllocationService(ParkingSlotRepository slotRepository) {
        this.slotRepository = slotRepository;
    }

    /**
     * Atomically allocates an available compatible slot for a vehicle.
     * Prevents race conditions and double-allocation by using conditional atomic database updates.
     */
    @Transactional
    public ParkingSlot allocateSlot(VehicleType vehicleType, String licensePlate, Long preferredFloorId, Long preferredZoneId) {
        // Step 1: Query candidates with preferred floor & zone
        List<ParkingSlot> candidates = slotRepository.findAvailableSlotsWithPreference(vehicleType, preferredFloorId, preferredZoneId);

        // Step 2: Fallback to all compatible slots across the facility if preference has no openings
        if (candidates.isEmpty()) {
            candidates = slotRepository.findAllAvailableByVehicleType(vehicleType);
        }

        if (candidates.isEmpty()) {
            throw new SlotNotAvailableException("All compatible parking bays for " + vehicleType + " are currently occupied.");
        }

        // Step 3: Try to claim a candidate atomically
        for (ParkingSlot candidate : candidates) {
            int rowsUpdated = slotRepository.updateSlotStatusIfMatches(
                    candidate.getId(),
                    SlotStatus.OCCUPIED,
                    true,
                    licensePlate,
                    SlotStatus.AVAILABLE
            );

            if (rowsUpdated > 0) {
                // Successfully secured this slot
                candidate.setStatus(SlotStatus.OCCUPIED);
                candidate.setIsOccupied(true);
                candidate.setCurrentVehiclePlate(licensePlate);
                return candidate;
            }
            // If rowsUpdated == 0, another concurrent transaction took this slot right before us.
            // Loop will continue to the next available candidate.
        }

        throw new SlotNotAvailableException("Could not reserve a slot due to high traffic contention. Please try again.");
    }

    /**
     * Frees an occupied slot back to AVAILABLE status.
     */
    @Transactional
    public void releaseSlot(ParkingSlot slot) {
        if (slot != null) {
            slotRepository.updateSlotStatusIfMatches(
                    slot.getId(),
                    SlotStatus.AVAILABLE,
                    false,
                    null,
                    SlotStatus.OCCUPIED
            );
            slot.setStatus(SlotStatus.AVAILABLE);
            slot.setIsOccupied(false);
            slot.setCurrentVehiclePlate(null);
        }
    }
}
