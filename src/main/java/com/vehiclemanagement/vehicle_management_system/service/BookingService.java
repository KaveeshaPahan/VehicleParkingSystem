package com.vehiclemanagement.vehicle_management_system.service;

import com.vehiclemanagement.vehicle_management_system.model.Booking;
import com.vehiclemanagement.vehicle_management_system.model.ParkingSlot;
import com.vehiclemanagement.vehicle_management_system.model.Payment;
import com.vehiclemanagement.vehicle_management_system.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    private final BookingRepository repo;
    private final ParkingSlotService slotService;
    private final PaymentService paymentService;

    public BookingService(BookingRepository repo, ParkingSlotService slotService,
                          PaymentService paymentService) {
        this.repo = repo;
        this.slotService = slotService;
        this.paymentService = paymentService;
    }

    public List<Booking> getAll() { return repo.findAll(); }

    public Optional<Booking> getById(String id) { return repo.findById(id); }

    public List<Booking> getByUser(String userId) {
        return repo.findAll().stream()
                .filter(b -> userId.equals(b.getUserId())).toList();
    }

    public List<Booking> getByStatus(String status) {
        return repo.findAll().stream()
                .filter(b -> status.equalsIgnoreCase(b.getStatus())).toList();
    }

    public Booking create(Booking booking) {
        if (booking.getStatus() == null || booking.getStatus().isBlank()) booking.setStatus("CONFIRMED");

        // auto compute total amount from slot rate * hours when not provided
        if (booking.getTotalAmount() <= 0 && booking.getSlotId() != null) {
            Optional<ParkingSlot> opt = slotService.getById(booking.getSlotId());
            if (opt.isPresent() && booking.getStartTime() != null && booking.getEndTime() != null) {
                long minutes = Duration.between(booking.getStartTime(), booking.getEndTime()).toMinutes();
                double hours = Math.max(1, Math.ceil(minutes / 60.0));
                booking.setTotalAmount(opt.get().getHourlyRate() * hours);
            }
        }

        Booking saved = repo.save(booking);
        // mark slot as occupied for an active booking
        if ("CONFIRMED".equalsIgnoreCase(saved.getStatus()) && saved.getSlotId() != null) {
            slotService.updateStatus(saved.getSlotId(), "OCCUPIED");
        }
        // auto-create a PENDING payment record for the booking
        if (!"CANCELLED".equalsIgnoreCase(saved.getStatus()) && saved.getTotalAmount() > 0) {
            Payment p = new Payment();
            p.setBookingId(saved.getId());
            p.setUserId(saved.getUserId());
            p.setAmount(saved.getTotalAmount());
            p.setMethod("CASH");
            p.setStatus("PENDING");
            paymentService.create(p);
        }
        return saved;
    }


    //update

    public Optional<Booking> update(String id, Booking booking) {
        booking.setId(id);
        Optional<Booking> updated = repo.update(booking);
        if (updated.isPresent()) {
            String status = updated.get().getStatus();
            String slotId = updated.get().getSlotId();
            if (slotId != null) {
                if ("CANCELLED".equalsIgnoreCase(status) || "COMPLETED".equalsIgnoreCase(status)) {
                    slotService.updateStatus(slotId, "AVAILABLE");
                } else if ("CONFIRMED".equalsIgnoreCase(status)) {
                    slotService.updateStatus(slotId, "OCCUPIED");
                }
            }
        }
        return updated;
    }

    //delete

    public boolean delete(String id) {
        Optional<Booking> b = repo.findById(id);
        boolean ok = repo.deleteById(id);
        if (ok && b.isPresent() && b.get().getSlotId() != null) {
            slotService.updateStatus(b.get().getSlotId(), "AVAILABLE");
        }
        return ok;
    }
}
