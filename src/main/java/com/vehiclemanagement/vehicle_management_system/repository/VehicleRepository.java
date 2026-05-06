package com.vehiclemanagement.vehicle_management_system.repository;

import com.vehiclemanagement.vehicle_management_system.model.Vehicle;
import org.springframework.stereotype.Repository;

@Repository
public class VehicleRepository extends FileRepository<Vehicle> {
    public VehicleRepository() {
        super("vehicles.txt", Vehicle::fromLine);
    }
}
