package com.vehiclemanagement.vehicle_management_system.model;

import java.time.LocalDateTime;

/**
 * Payment entity - tracks money paid against bookings.
 * Extends BaseEntity (inheritance) and demonstrates encapsulation.
 */
public class Payment extends BaseEntity {

    private String bookingId;
    private String userId;
    private double amount;
    private String method;        // CASH, CARD, ONLINE
    private String transactionId; // optional reference
    private String status;        // PENDING, PAID, REFUNDED, FAILED
    private LocalDateTime paidAt;
    private String notes;


    // Default no-argument constructor initializing base properties
    public Payment() { super(); }

    // Parameterized constructor to instantiate a payment with details
    public Payment(String bookingId, String userId, double amount, String method,
                   String transactionId, String status, LocalDateTime paidAt, String notes) {
        super();
        this.bookingId = bookingId;
        this.userId = userId;
        this.amount = amount;
        this.method = method;
        this.transactionId = transactionId;
        this.status = status;
        this.paidAt = paidAt;
        this.notes = notes;
    }

    // Getter and setter methods providing controlled access to private fields (Encapsulation)
    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // Serializes the object fields into a single delimited string for file storage
    @Override
    public String toLine() {
        return String.join(SEP,
                safe(getId()),
                safe(bookingId),
                safe(userId),
                String.valueOf(amount),
                safe(method),
                safe(transactionId),
                safe(status),
                paidAt == null ? "" : paidAt.format(DATE_FORMAT),
                safe(notes),
                getCreatedAt().format(DATE_FORMAT),
                getUpdatedAt().format(DATE_FORMAT));
    }

    // Deserializes a delimited string line back into a structured Payment object
    public static Payment fromLine(String line) {
        String[] p = line.split(DELIM, -1);

        // Ensure the array contains all required data attributes before parsing
        if (p.length < 11) return null;
        Payment x = new Payment();
        x.setId(p[0]);
        x.setBookingId(p[1]);
        x.setUserId(p[2]);

        // Safely parse the decimal value for amount with a fallback to zero on error
        try { x.setAmount(Double.parseDouble(p[3])); } catch (NumberFormatException e) { x.setAmount(0.0); }
        x.setMethod(p[4]);
        x.setTransactionId(p[5]);
        x.setStatus(p[6]);

        // Parse date values from string tokens back into LocalDateTime objects
        x.setPaidAt(p[7].isEmpty() ? null : LocalDateTime.parse(p[7], DATE_FORMAT));
        x.setNotes(p[8]);
        x.setCreatedAt(LocalDateTime.parse(p[9], DATE_FORMAT));
        x.setUpdatedAt(LocalDateTime.parse(p[10], DATE_FORMAT));
        return x;
    }
}
