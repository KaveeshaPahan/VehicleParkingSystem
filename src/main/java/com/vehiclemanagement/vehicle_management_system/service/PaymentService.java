package com.vehiclemanagement.vehicle_management_system.service;

import com.vehiclemanagement.vehicle_management_system.model.Payment;
import com.vehiclemanagement.vehicle_management_system.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

// Marks this class as a Spring service handling the core business logic for parking payments
@Service
public class PaymentService {

    private final PaymentRepository repo;


    // Constructor injection to connect the database storage repository
    public PaymentService(PaymentRepository repo) {
        this.repo = repo;
    }

    // Fetches all payment records stored in the system
    public List<Payment> getAll() { return repo.findAll(); }

    // Finds a specific payment record by its unique identifier
    public Optional<Payment> getById(String id) { return repo.findById(id); }

    // Filters and returns all payments associated with a specific parking booking ID
    public List<Payment> getByBooking(String bookingId) {
        return repo.findAll().stream()
                .filter(p -> bookingId.equals(p.getBookingId())).toList();
    }

    // Filters and returns all payments processed by a specific customer ID
    public List<Payment> getByUser(String userId) {
        return repo.findAll().stream()
                .filter(p -> userId.equals(p.getUserId())).toList();
    }

    // Filters and returns payments matching a target state, ignoring letter case
    public List<Payment> getByStatus(String status) {
        return repo.findAll().stream()
                .filter(p -> status.equalsIgnoreCase(p.getStatus())).toList();
    }

    // Searches across text attributes like payment method, status, transaction ID, and notes
    public List<Payment> search(String query) {
        if (query == null || query.isBlank()) return getAll();
        String q = query.toLowerCase();
        return repo.findAll().stream().filter(p ->
                (p.getMethod() != null && p.getMethod().toLowerCase().contains(q)) ||
                (p.getStatus() != null && p.getStatus().toLowerCase().contains(q)) ||
                (p.getTransactionId() != null && p.getTransactionId().toLowerCase().contains(q)) ||
                (p.getNotes() != null && p.getNotes().toLowerCase().contains(q))
        ).toList();
    }

    // Sets default fields, timestamps successful payments, and saves the new transaction record
    public Payment create(Payment payment) {
        if (payment.getStatus() == null || payment.getStatus().isBlank()) payment.setStatus("PENDING");
        if (payment.getMethod() == null || payment.getMethod().isBlank()) payment.setMethod("CASH");
        if ("PAID".equalsIgnoreCase(payment.getStatus()) && payment.getPaidAt() == null) {
            payment.setPaidAt(LocalDateTime.now());
        }
        return repo.save(payment);
    }

    // Updates an existing payment record and sets the completion timestamp if newly marked as PAID
    public Optional<Payment> update(String id, Payment payment) {
        payment.setId(id);
        if ("PAID".equalsIgnoreCase(payment.getStatus()) && payment.getPaidAt() == null) {
            payment.setPaidAt(LocalDateTime.now());
        }
        return repo.update(payment);
    }

    // Removes a payment record from storage by its ID and returns true if successful
    public boolean delete(String id) { return repo.deleteById(id); }
}
