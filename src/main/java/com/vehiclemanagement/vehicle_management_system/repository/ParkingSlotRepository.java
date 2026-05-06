package com.vehiclemanagement.vehicle_management_system.repository;

import com.vehiclemanagement.vehicle_management_system.model.ParkingSlot;
import org.springframework.stereotype.Repository;

@Repository
public class ParkingSlotRepository extends FileRepository<ParkingSlot> {
    public ParkingSlotRepository() {
        super("parking_slots.txt", ParkingSlot::fromLine);
    }
}
