package com.vehiclemanagement.vehicle_management_system.service;

import com.vehiclemanagement.vehicle_management_system.model.Booking;
import com.vehiclemanagement.vehicle_management_system.model.ParkingSlot;
import com.vehiclemanagement.vehicle_management_system.model.Payment;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final UserService userService;
    private final VehicleService vehicleService;
    private final ParkingSlotService slotService;
    private final BookingService bookingService;
    private final FeedbackService feedbackService;
    private final PaymentService paymentService;

    public ReportService(UserService userService, VehicleService vehicleService,
                         ParkingSlotService slotService, BookingService bookingService,
                         FeedbackService feedbackService, PaymentService paymentService) {
        this.userService = userService;
        this.vehicleService = vehicleService;
        this.slotService = slotService;
        this.bookingService = bookingService;
        this.feedbackService = feedbackService;
        this.paymentService = paymentService;
    }

    /** High-level dashboard stats. */
    public Map<String, Object> dashboard() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("totalUsers", userService.getAll().size());
        map.put("totalVehicles", vehicleService.getAll().size());

        List<ParkingSlot> slots = slotService.getAll();
        map.put("totalSlots", slots.size());
        map.put("availableSlots", slots.stream()
                .filter(s -> "AVAILABLE".equalsIgnoreCase(s.getStatus())).count());
        map.put("occupiedSlots", slots.stream()
                .filter(s -> "OCCUPIED".equalsIgnoreCase(s.getStatus())).count());

        List<Booking> bookings = bookingService.getAll();
        map.put("totalBookings", bookings.size());
        map.put("activeBookings", bookings.stream()
                .filter(b -> "CONFIRMED".equalsIgnoreCase(b.getStatus())).count());
        map.put("completedBookings", bookings.stream()
                .filter(b -> "COMPLETED".equalsIgnoreCase(b.getStatus())).count());
        map.put("cancelledBookings", bookings.stream()
                .filter(b -> "CANCELLED".equalsIgnoreCase(b.getStatus())).count());

        double revenue = bookings.stream()
                .filter(b -> !"CANCELLED".equalsIgnoreCase(b.getStatus()))
                .mapToDouble(Booking::getTotalAmount).sum();
        map.put("totalRevenue", revenue);

        map.put("totalFeedback", feedbackService.getAll().size());
        double avgRating = feedbackService.getAll().stream()
                .mapToInt(f -> f.getRating()).average().orElse(0.0);
        map.put("averageRating", Math.round(avgRating * 10.0) / 10.0);

        List<Payment> payments = paymentService.getAll();
        map.put("totalPayments", payments.size());
        double paid = payments.stream()
                .filter(p -> "PAID".equalsIgnoreCase(p.getStatus()))
                .mapToDouble(Payment::getAmount).sum();
        double pending = payments.stream()
                .filter(p -> "PENDING".equalsIgnoreCase(p.getStatus()))
                .mapToDouble(Payment::getAmount).sum();
        map.put("paidAmount", paid);
        map.put("pendingAmount", pending);

        return map;
    }

    /** Payments grouped by method (CASH/CARD/ONLINE). */
    public Map<String, Long> paymentsByMethod() {
        Map<String, Long> map = new HashMap<>();
        paymentService.getAll().forEach(p ->
                map.merge(p.getMethod() == null ? "UNKNOWN" : p.getMethod().toUpperCase(), 1L, Long::sum));
        return map;
    }

    /** History of all bookings sorted by created date (newest first). */
    public List<Booking> bookingHistory() {
        return bookingService.getAll().stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .toList();
    }

    /** Revenue grouped by date (yyyy-MM-dd). */
    public Map<String, Double> revenueByDate() {
        Map<String, Double> agg = new LinkedHashMap<>();
        for (Booking b : bookingService.getAll()) {
            if ("CANCELLED".equalsIgnoreCase(b.getStatus())) continue;
            LocalDate d = b.getCreatedAt().toLocalDate();
            agg.merge(d.toString(), b.getTotalAmount(), Double::sum);
        }
        return agg;
    }

    /** Bookings count grouped by status. */
    public Map<String, Long> bookingsByStatus() {
        Map<String, Long> map = new HashMap<>();
        for (Booking b : bookingService.getAll()) {
            map.merge(b.getStatus() == null ? "UNKNOWN" : b.getStatus().toUpperCase(), 1L, Long::sum);
        }
        return map;
    }

    /** Vehicles count grouped by type. */
    public Map<String, Long> vehiclesByType() {
        Map<String, Long> map = new HashMap<>();
        vehicleService.getAll().forEach(v ->
                map.merge(v.getType() == null ? "UNKNOWN" : v.getType().toUpperCase(), 1L, Long::sum));
        return map;
    }
}