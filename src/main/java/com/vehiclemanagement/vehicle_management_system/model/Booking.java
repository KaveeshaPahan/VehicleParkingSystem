package com.vehiclemanagement.vehicle_management_system.model;

import java.time.LocalDateTime;

public class Booking extends BaseEntity {

    private String userId;
    private String vehicleId;
    private String slotId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private double totalAmount;
    private String status;     // PENDING, CONFIRMED, COMPLETED, CANCELLED

    public Booking() { super(); }

    public Booking(String userId, String vehicleId, String slotId,
                   LocalDateTime startTime, LocalDateTime endTime,
                   double totalAmount, String status) {
        super();
        this.userId = userId;
        this.vehicleId = vehicleId;
        this.slotId = slotId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getVehicleId() { return vehicleId; }
    public void setVehicleId(String vehicleId) { this.vehicleId = vehicleId; }

    public String getSlotId() { return slotId; }
    public void setSlotId(String slotId) { this.slotId = slotId; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toLine() {
        return String.join(SEP,
                safe(getId()),
                safe(userId),
                safe(vehicleId),
                safe(slotId),
                startTime.format(DATE_FORMAT),
                endTime.format(DATE_FORMAT),
                String.valueOf(totalAmount),
                safe(status),
                getCreatedAt().format(DATE_FORMAT),
                getUpdatedAt().format(DATE_FORMAT));
    }

    public static Booking fromLine(String line) {
        String[] p = line.split(DELIM, -1);
        if (p.length < 10) return null;
        Booking b = new Booking();
        b.setId(p[0]);
        b.setUserId(p[1]);
        b.setVehicleId(p[2]);
        b.setSlotId(p[3]);
        b.setStartTime(LocalDateTime.parse(p[4], DATE_FORMAT));
        b.setEndTime(LocalDateTime.parse(p[5], DATE_FORMAT));
        try { b.setTotalAmount(Double.parseDouble(p[6])); } catch (NumberFormatException e) { b.setTotalAmount(0.0); }
        b.setStatus(p[7]);
        b.setCreatedAt(LocalDateTime.parse(p[8], DATE_FORMAT));
        b.setUpdatedAt(LocalDateTime.parse(p[9], DATE_FORMAT));
        return b;
    }
}
