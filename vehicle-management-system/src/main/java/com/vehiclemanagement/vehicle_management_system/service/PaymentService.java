package com.vehiclemanagement.vehicle_management_system.service;

import com.vehiclemanagement.vehicle_management_system.model.Payment;
import com.vehiclemanagement.vehicle_management_system.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {

    private final PaymentRepository repo;

    public PaymentService(PaymentRepository repo) {
        this.repo = repo;
    }

    public List<Payment> getAll() { return repo.findAll(); }

    public Optional<Payment> getById(String id) { return repo.findById(id); }

    public List<Payment> getByBooking(String bookingId) {
        return repo.findAll().stream()
                .filter(p -> bookingId.equals(p.getBookingId())).toList();
    }

    public List<Payment> getByUser(String userId) {
        return repo.findAll().stream()
                .filter(p -> userId.equals(p.getUserId())).toList();
    }

    public List<Payment> getByStatus(String status) {
        return repo.findAll().stream()
                .filter(p -> status.equalsIgnoreCase(p.getStatus())).toList();
    }

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

    public Payment create(Payment payment) {
        if (payment.getStatus() == null || payment.getStatus().isBlank()) payment.setStatus("PENDING");
        if (payment.getMethod() == null || payment.getMethod().isBlank()) payment.setMethod("CASH");
        if ("PAID".equalsIgnoreCase(payment.getStatus()) && payment.getPaidAt() == null) {
            payment.setPaidAt(LocalDateTime.now());
        }
        return repo.save(payment);
    }

    public Optional<Payment> update(String id, Payment payment) {
        payment.setId(id);
        if ("PAID".equalsIgnoreCase(payment.getStatus()) && payment.getPaidAt() == null) {
            payment.setPaidAt(LocalDateTime.now());
        }
        return repo.update(payment);
    }

    public boolean delete(String id) { return repo.deleteById(id); }
}
