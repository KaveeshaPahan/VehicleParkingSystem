package com.vehiclemanagement.vehicle_management_system.controller;

import com.vehiclemanagement.vehicle_management_system.model.Payment;
import com.vehiclemanagement.vehicle_management_system.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// REST Controller to manage parking payment transactions
@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {

    private final PaymentService paymentService;

    // Injecting the payment service dependency via constructor
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // Retrieve payments with optional filters (bookingId, userId, status, or search query)
    @GetMapping
    public List<Payment> all(@RequestParam(value = "q", required = false) String q,
                             @RequestParam(value = "userId", required = false) String userId,
                             @RequestParam(value = "bookingId", required = false) String bookingId,
                             @RequestParam(value = "status", required = false) String status) {
        if (bookingId != null && !bookingId.isBlank()) return paymentService.getByBooking(bookingId);
        if (userId != null && !userId.isBlank()) return paymentService.getByUser(userId);
        if (status != null && !status.isBlank()) return paymentService.getByStatus(status);
        return q == null ? paymentService.getAll() : paymentService.search(q);
    }
    // Fetch a specific payment record by its unique ID
    @GetMapping("/{id}")
    public ResponseEntity<Payment> byId(@PathVariable String id) {
        return paymentService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // Process and create a new parking payment record

    @PostMapping
    public ResponseEntity<Payment> create(@RequestBody Payment p) {
        if (p.getBookingId() == null || p.getBookingId().isBlank() || p.getAmount() <= 0) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(paymentService.create(p));
    }

    // Update an existing payment record by its ID
    @PutMapping("/{id}")
    public ResponseEntity<Payment> update(@PathVariable String id, @RequestBody Payment p) {
        return paymentService.update(id, p)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // Delete a payment record from the system by its ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String id) {
        if (!paymentService.delete(id)) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("deleted", true, "id", id));
    }
}
