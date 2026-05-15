package com.vehiclemanagement.vehicle_management_system.model;

import java.time.LocalDateTime;

/**
 * User entity - INHERITS from BaseEntity. Demonstrates encapsulation
 * with private fields and public accessors.
 */
public class User extends BaseEntity {

    //Private Variables (Booking details)
    private String name;
    private String email;
    private String phone;
    private String address;
    private String role;      // ADMIN or CUSTOMER
    private String password;

    public User() { super(); }

    // Full constructor to set up a booking with all details at once
    public User(String name, String email, String phone, String address, String role, String password) {
        super();
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.role = role;
        this.password = password;
    }

    //Getters and Setters (Standard methods to get/set data)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    //Converts this Booking into a single string for storage.
    //Uses separators to keep the data organized in a text file.
    @Override
    public String toLine() {
        return String.join(SEP,
                safe(getId()),
                safe(name),
                safe(email),
                safe(phone),
                safe(address),
                safe(role),
                safe(password),
                getCreatedAt().format(DATE_FORMAT),
                getUpdatedAt().format(DATE_FORMAT));
    }

    //Reads a line of text and rebuilds the Booking object.
    public static User fromLine(String line) {
        String[] p = line.split(DELIM, -1);
        // Ensure there are at least 9 pieces of data in the line
        if (p.length < 9) return null;
        User u = new User();
        u.setId(p[0]);
        u.setName(p[1]);
        u.setEmail(p[2]);
        u.setPhone(p[3]);
        u.setAddress(p[4]);
        u.setRole(p[5]);
        u.setPassword(p[6]);

        // Converts text dates back into actual Java LocalDateTime objects
        u.setCreatedAt(LocalDateTime.parse(p[7], DATE_FORMAT));
        u.setUpdatedAt(LocalDateTime.parse(p[8], DATE_FORMAT));
        return u;
    }
}
