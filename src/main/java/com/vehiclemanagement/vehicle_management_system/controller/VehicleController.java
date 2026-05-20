package com.vehiclemanagement.vehicle_management_system.controller;

import com.vehiclemanagement.vehicle_management_system.model.Vehicle;
import com.vehiclemanagement.vehicle_management_system.service.VehicleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    //view vehicle page without any filters
    @GetMapping
    public List<Vehicle> all(@RequestParam(value = "q", required = false) String q,
                             @RequestParam(value = "ownerId", required = false) String ownerId) {
        if (ownerId != null && !ownerId.isBlank()) return vehicleService.getByOwner(ownerId);
        return q == null ? vehicleService.getAll() : vehicleService.search(q);
    }

    //view specific vehicle details by id
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> byId(@PathVariable String id) {
        return vehicleService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // create vehicle
    @PostMapping
    public ResponseEntity<Vehicle> create(@RequestBody Vehicle v) {
        if (v.getPlateNumber() == null || v.getPlateNumber().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(vehicleService.create(v));
    }

    //update vehicle
    @PutMapping("/{id}")
    public ResponseEntity<Vehicle> update(@PathVariable String id, @RequestBody Vehicle v) {
        return vehicleService.update(id, v)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //delete vehicle
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String id) {
        if (!vehicleService.delete(id)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("deleted", true, "id", id));
    }
}
