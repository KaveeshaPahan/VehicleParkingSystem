package com.vehiclemanagement.vehicle_management_system.controller;

import com.vehiclemanagement.vehicle_management_system.model.Booking;
import com.vehiclemanagement.vehicle_management_system.service.ReportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    // Connects the Service layer to this Controller (Dependency Injection)
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    // Gets a general overview of the system for the main dashboard
    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return reportService.dashboard();
    }

    // Returns a list of every booking ever made (the audit trail)
    @GetMapping("/history")
    public List<Booking> history() {
        return reportService.bookingHistory();
    }

    // Shows how much money was earned, grouped by dates
    @GetMapping("/revenue")
    public Map<String, Double> revenue() {
        return reportService.revenueByDate();
    }

    // Counts how many bookings are 'Pending', 'Confirmed', or 'Cancelled'
    @GetMapping("/bookings-by-status")
    public Map<String, Long> bookingsByStatus() {
        return reportService.bookingsByStatus();
    }

    // Shows the count of vehicles based on their category (e.g., Sedan, SUV, Truck)
    @GetMapping("/vehicles-by-type")
    public Map<String, Long> vehiclesByType() {
        return reportService.vehiclesByType();
    }

    // Shows which payment methods are used most (e.g., Cash, Card)
    @GetMapping("/payments-by-method")
    public Map<String, Long> paymentsByMethod() {
        return reportService.paymentsByMethod();
    }
}