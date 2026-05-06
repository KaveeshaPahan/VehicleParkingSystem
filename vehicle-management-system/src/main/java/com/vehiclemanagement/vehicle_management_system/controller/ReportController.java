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

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/dashboard")
    public Map<String, Object> dashboard() {
        return reportService.dashboard();
    }

    @GetMapping("/history")
    public List<Booking> history() {
        return reportService.bookingHistory();
    }

    @GetMapping("/revenue")
    public Map<String, Double> revenue() {
        return reportService.revenueByDate();
    }

    @GetMapping("/bookings-by-status")
    public Map<String, Long> bookingsByStatus() {
        return reportService.bookingsByStatus();
    }

    @GetMapping("/vehicles-by-type")
    public Map<String, Long> vehiclesByType() {
        return reportService.vehiclesByType();
    }

    @GetMapping("/payments-by-method")
    public Map<String, Long> paymentsByMethod() {
        return reportService.paymentsByMethod();
    }
}
