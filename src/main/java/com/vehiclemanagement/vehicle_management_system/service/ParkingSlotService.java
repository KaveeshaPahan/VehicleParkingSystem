package com.vehiclemanagement.vehicle_management_system.service;

import com.vehiclemanagement.vehicle_management_system.model.ParkingSlot;
import com.vehiclemanagement.vehicle_management_system.repository.ParkingSlotRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ParkingSlotService {

    private final ParkingSlotRepository repo;       // Data access layer for ParkingSlot entities.
                                                    // Marked 'final' to ensure immutability and thread safety after dependency injection.

    // Constructor injection
    public ParkingSlotService(ParkingSlotRepository repo) {

        this.repo = repo;
    }
    
    //Retrieves a list of all parking slots from the database
    public List<ParkingSlot> getAll() {
        return repo.findAll(); }

    public Optional<ParkingSlot> getById(String id) { return repo.findById(id); }

    public List<ParkingSlot> getAvailable() {
        return repo.findAll().stream()
                .filter(s -> "AVAILABLE".equalsIgnoreCase(s.getStatus())).toList();
    }

    public List<ParkingSlot> search(String query) {
        if (query == null || query.isBlank()) return getAll();
        String q = query.toLowerCase();
        return repo.findAll().stream().filter(s ->
                (s.getSlotNumber() != null && s.getSlotNumber().toLowerCase().contains(q)) ||
                (s.getLocation() != null && s.getLocation().toLowerCase().contains(q)) ||
                (s.getType() != null && s.getType().toLowerCase().contains(q)) ||
                (s.getStatus() != null && s.getStatus().toLowerCase().contains(q))
        ).toList();
    }

    public ParkingSlot create(ParkingSlot slot) {
        if (slot.getStatus() == null || slot.getStatus().isBlank()) slot.setStatus("AVAILABLE");
        return repo.save(slot);
    }

    public Optional<ParkingSlot> update(String id, ParkingSlot slot) {
        slot.setId(id);
        return repo.update(slot);
    }

    public boolean updateStatus(String id, String status) {
        Optional<ParkingSlot> opt = repo.findById(id);
        if (opt.isEmpty()) return false;
        ParkingSlot s = opt.get();
        s.setStatus(status);
        return repo.update(s).isPresent();
    }

    public boolean delete(String id) { return repo.deleteById(id); }
}
