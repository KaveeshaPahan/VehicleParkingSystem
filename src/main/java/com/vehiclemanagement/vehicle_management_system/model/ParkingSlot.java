package com.vehiclemanagement.vehicle_management_system.model;

import java.time.LocalDateTime;     // Used for tracking timestamps like booking starts, ends, and durations

//inheritance
public class ParkingSlot extends BaseEntity {

    private String slotNumber;
    private String location;
    private String type;        // CAR, BIKE, VAN, TRUCK
    private double hourlyRate;
    private String status;      // AVAILABLE, OCCUPIED, MAINTENANCE

    //Constructor Overloading-providing multiple ways to initialize an object
    //Default Constructor

    public ParkingSlot() {             //creates a blank, empty ParkingSlot object without setting any data upfront.

        super();
    }

    //Parameterized Constructor-use when creating a brand-new parking slot. It lets you pass in all the required details right at birth.
    public ParkingSlot(String slotNumber, String location, String type,
                       double hourlyRate, String status) {
        super();                        //call parent class constructor
        this.slotNumber = slotNumber;
        this.location = location;
        this.type = type;
        this.hourlyRate = hourlyRate;
        this.status = status;
    }

    //getters and setters
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

    //toLine method
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

    //fromLine method-converts ParkingSlot object into a single, structured line of text
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
