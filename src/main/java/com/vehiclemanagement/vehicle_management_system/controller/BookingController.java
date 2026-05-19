package com.vehiclemanagement.vehicle_management_system.controller;

import com.vehiclemanagement.vehicle_management_system.model.Booking;
import com.vehiclemanagement.vehicle_management_system.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }


    //all
    @GetMapping
    public List<Booking> all(@RequestParam(value = "userId", required = false) String userId,
                             @RequestParam(value = "status", required = false) String status) {
        if (userId != null && !userId.isBlank()) return bookingService.getByUser(userId);
        if (status != null && !status.isBlank()) return bookingService.getByStatus(status);
        return bookingService.getAll();
    }


    //getbyid
    @GetMapping("/{id}")
    public ResponseEntity<Booking> byId(@PathVariable String id) {
        return bookingService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    //postrequest
    @PostMapping
    public ResponseEntity<Booking> create(@RequestBody Booking booking) {
        if (booking.getUserId() == null || booking.getSlotId() == null
                || booking.getStartTime() == null || booking.getEndTime() == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(bookingService.create(booking));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Booking> update(@PathVariable String id, @RequestBody Booking booking) {
        return bookingService.update(id, booking)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
//delete Booking 
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String id) {
        if (!bookingService.delete(id)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("deleted", true, "id", id));
    }
}
