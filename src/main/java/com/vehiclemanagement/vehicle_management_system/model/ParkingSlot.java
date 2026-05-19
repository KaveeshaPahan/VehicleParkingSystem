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

    //toLine method--converts ParkingSlot object into a single, structured line of text
    @Override
    public String toLine() {
        return String.join(SEP,         //takes a list of text pieces and glues them together using a separator
                safe(getId()),          //checks if a value is null, and if it is, it converts it into an empty string "" or "N/A" so system doesn't crash with a NullPointerException
                safe(slotNumber),
                safe(location),
                safe(type),
                String.valueOf(hourlyRate),     //converts the number (e.g., 15.50) into plain text ("15.50").
                safe(status),
                getCreatedAt().format(DATE_FORMAT),
                getUpdatedAt().format(DATE_FORMAT));
    }

    //fromLine method--taking a single raw line of text (like a row read out of a .csv or .txt database file) and rebuilding a complete Java ParkingSlot object out of it.
    public static ParkingSlot fromLine(String line) {
        String[] p = line.split(DELIM, -1);         //breaks the text string into an array of smaller strings (p) wherever it finds your delimiter (like a comma)
        if (p.length < 8) return null;
        ParkingSlot s = new ParkingSlot();
        s.setId(p[0]);
        s.setSlotNumber(p[1]);
        s.setLocation(p[2]);
        s.setType(p[3]);
        try { s.setHourlyRate(Double.parseDouble(p[4])); } catch (NumberFormatException e) { s.setHourlyRate(0.0); } //To prevent the program from crashing
        s.setStatus(p[5]);
        s.setCreatedAt(LocalDateTime.parse(p[6], DATE_FORMAT));
        s.setUpdatedAt(LocalDateTime.parse(p[7], DATE_FORMAT));
        return s;
    }
}
