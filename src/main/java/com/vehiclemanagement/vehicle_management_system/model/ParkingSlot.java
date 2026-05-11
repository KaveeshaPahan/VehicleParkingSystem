package com.vehiclemanagement.vehicle_management_system.model;

import java.time.LocalDateTime;

//inheritance
public class ParkingSlot extends BaseEntity {

    private String slotNumber;
    private String location;
    private String type;        // CAR, BIKE, VAN, TRUCK
    private double hourlyRate;
    private String status;      // AVAILABLE, OCCUPIED, MAINTENANCE

    public ParkingSlot() { super(); }

    public ParkingSlot(String slotNumber, String location, String type,
                       double hourlyRate, String status) {
        super();
        this.slotNumber = slotNumber;
        this.location = location;
        this.type = type;
        this.hourlyRate = hourlyRate;
        this.status = status;
    }

    public String getSlotNumber() { return slotNumber; }
    public void setSlotNumber(String slotNumber) { this.slotNumber = slotNumber; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toLine() {
        return String.join(SEP,
                safe(getId()),
                safe(slotNumber),
                safe(location),
                safe(type),
                String.valueOf(hourlyRate),
                safe(status),
                getCreatedAt().format(DATE_FORMAT),
                getUpdatedAt().format(DATE_FORMAT));
    }

    public static ParkingSlot fromLine(String line) {
        String[] p = line.split(DELIM, -1);
        if (p.length < 8) return null;
        ParkingSlot s = new ParkingSlot();
        s.setId(p[0]);
        s.setSlotNumber(p[1]);
        s.setLocation(p[2]);
        s.setType(p[3]);
        try { s.setHourlyRate(Double.parseDouble(p[4])); } catch (NumberFormatException e) { s.setHourlyRate(0.0); }
        s.setStatus(p[5]);
        s.setCreatedAt(LocalDateTime.parse(p[6], DATE_FORMAT));
        s.setUpdatedAt(LocalDateTime.parse(p[7], DATE_FORMAT));
        return s;
    }
}
