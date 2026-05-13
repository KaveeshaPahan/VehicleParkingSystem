package com.vehiclemanagement.vehicle_management_system.model;

import java.time.LocalDateTime;

public class Vehicle extends BaseEntity {

    private String ownerID;       // User id
    private String plateNumber;
    private String brand;
    private String model;
    private String type;          // CAR, BIKE, VAN, TRUCK
    private String color;
    private int year;

    public Vehicle() { super(); }

    public Vehicle(String ownerId, String plateNumber, String brand, String model,
                   String type, String color, int year) {
        super();
        this.ownerID = ownerId;
        this.plateNumber = plateNumber;
        this.brand = brand;
        this.model = model;
        this.type = type;
        this.color = color;
        this.year = year;
    }

    public String getOwnerId() { return ownerID; }
    public void setOwnerId(String ownerId) { this.ownerID = ownerId; }

    public String getPlateNumber() { return plateNumber; }
    public void setPlateNumber(String plateNumber) { this.plateNumber = plateNumber; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    @Override
    public String toLine() {
        return String.join(SEP,
                safe(getId()),
                safe(ownerID),
                safe(plateNumber),
                safe(brand),
                safe(model),
                safe(type),
                safe(color),
                String.valueOf(year),
                getCreatedAt().format(DATE_FORMAT),
                getUpdatedAt().format(DATE_FORMAT));
    }

    public static Vehicle fromLine(String line) {
        String[] p = line.split(DELIM, -1);
        if (p.length < 10) return null;
        Vehicle v = new Vehicle();
        v.setId(p[0]);
        v.setOwnerId(p[1]);
        v.setPlateNumber(p[2]);
        v.setBrand(p[3]);
        v.setModel(p[4]);
        v.setType(p[5]);
        v.setColor(p[6]);
        try { v.setYear(Integer.parseInt(p[7])); } catch (NumberFormatException e) { v.setYear(0); }
        v.setCreatedAt(LocalDateTime.parse(p[8], DATE_FORMAT));
        v.setUpdatedAt(LocalDateTime.parse(p[9], DATE_FORMAT));
        return v;
    }
}
