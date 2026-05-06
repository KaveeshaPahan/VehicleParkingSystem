package com.vehiclemanagement.vehicle_management_system.service;

import com.vehiclemanagement.vehicle_management_system.model.Vehicle;
import com.vehiclemanagement.vehicle_management_system.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public List<Vehicle> getAll() { return vehicleRepository.findAll(); }

    public Optional<Vehicle> getById(String id) { return vehicleRepository.findById(id); }

    public List<Vehicle> search(String query) {
        if (query == null || query.isBlank()) return getAll();
        String q = query.toLowerCase();
        return vehicleRepository.findAll().stream().filter(v ->
                (v.getPlateNumber() != null && v.getPlateNumber().toLowerCase().contains(q)) ||
                (v.getBrand() != null && v.getBrand().toLowerCase().contains(q)) ||
                (v.getModel() != null && v.getModel().toLowerCase().contains(q)) ||
                (v.getType() != null && v.getType().toLowerCase().contains(q))
        ).toList();
    }

    public List<Vehicle> getByOwner(String ownerId) {
        return vehicleRepository.findAll().stream()
                .filter(v -> ownerId.equals(v.getOwnerId())).toList();
    }

    public Vehicle create(Vehicle vehicle) { return vehicleRepository.save(vehicle); }

    public Optional<Vehicle> update(String id, Vehicle vehicle) {
        vehicle.setId(id);
        return vehicleRepository.update(vehicle);
    }

    public boolean delete(String id) { return vehicleRepository.deleteById(id); }
}
