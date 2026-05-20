package com.vehiclemanagement.vehicle_management_system.controller;

import com.vehiclemanagement.vehicle_management_system.model.ParkingSlot;
import com.vehiclemanagement.vehicle_management_system.service.ParkingSlotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController               //Marks this class as a REST API controller. Automatically converts return values to JSON
@RequestMapping("/api/slots")   //All endpoints in this class start with /api/slots
@CrossOrigin(origins = "*")     //Allows requests from any domain (frontend on a different port can still call this API)
public class ParkingSlotController {

    //Dependency Injection

    private final ParkingSlotService slotService;

    public ParkingSlotController(ParkingSlotService slotService) {

        this.slotService = slotService;
    }

    //get all or search
    @GetMapping
    public List<ParkingSlot> all(@RequestParam(value = "q", required = false) String q,
                                 @RequestParam(value = "available", required = false) Boolean available) {
        if (Boolean.TRUE.equals(available)) return slotService.getAvailable();
        return q == null ? slotService.getAll() : slotService.search(q);
    }

    //get a slot by id
    @GetMapping("/{id}")
    public ResponseEntity<ParkingSlot> byId(@PathVariable String id) {
        return slotService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    //create vehicle slot
    @PostMapping
    public ResponseEntity<ParkingSlot> create(@RequestBody ParkingSlot slot) {
        if (slot.getSlotNumber() == null || slot.getSlotNumber().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(slotService.create(slot));
    }
    //update vehicle slot
    @PutMapping("/{id}")
    public ResponseEntity<ParkingSlot> update(@PathVariable String id, @RequestBody ParkingSlot slot) {
        return slotService.update(id, slot)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    //delete vehicle slot
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String id) {
        if (!slotService.delete(id)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("deleted", true, "id", id));
    }
}
