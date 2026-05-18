package com.vehiclemanagement.vehicle_management_system.model;

import java.time.LocalDateTime;

// Model class representing feedback information in the system
public class Feedback extends BaseEntity {

    // Stores the ID of the user who submitted feedback
    private String userId;
    // Stores the subject/title of the feedback
    private String subject;
    // Stores the detailed feedback message
    private String message;
    // Stores user rating value from 1 to 5
    private int rating; // Stores feedback status such as OPEN, REVIEWED, or RESOLVED
    private String status;     // OPEN, REVIEWED, RESOLVED

    // Default constructor
    public Feedback() { super(); }

    // Parameterized constructor used to initialize feedback details
    public Feedback(String userId, String subject, String message, int rating, String status) {
        super();
        this.userId = userId;
        this.subject = subject;
        this.message = message;
        this.rating = rating;
        this.status = status;
    }

    public String getUserId() { return userId; }  // Returns user ID
    public void setUserId(String userId) { this.userId = userId; } // Sets user ID

    public String getSubject() { return subject; } // Returns feedback subject
    public void setSubject(String subject) { this.subject = subject; } // Sets feedback subject

    public String getMessage() { return message; }  // Returns feedback message
    public void setMessage(String message) { this.message = message; }  // Sets feedback message

    public int getRating() { return rating; } // Returns feedback rating
    public void setRating(int rating) { this.rating = rating; }  // Sets feedback rating

    public String getStatus() { return status; }  // Returns feedback status
    public void setStatus(String status) { this.status = status; }  // Sets feedback status

    // Converts Feedback object into a text line for file storage
    @Override
    public String toLine() {
        return String.join(SEP,
                safe(getId()),
                safe(userId),
                safe(subject),
                safe(message),
                String.valueOf(rating),
                safe(status),
                getCreatedAt().format(DATE_FORMAT),
                getUpdatedAt().format(DATE_FORMAT));
    }
    // Creates a Feedback object from a stored text line
    public static Feedback fromLine(String line) {
        // Split stored data into parts
        String[] p = line.split(DELIM, -1);
        // Return null if data format is invalid
        if (p.length < 8) return null;
        Feedback f = new Feedback();

        // Set feedback properties from stored values
        f.setId(p[0]);
        f.setUserId(p[1]);
        f.setSubject(p[2]);
        f.setMessage(p[3]);
        // Convert rating safely to integer
        try { f.setRating(Integer.parseInt(p[4])); } catch (NumberFormatException e) { f.setRating(0); }

        // Set remaining feedback details
        f.setStatus(p[5]);
        f.setCreatedAt(LocalDateTime.parse(p[6], DATE_FORMAT));
        f.setUpdatedAt(LocalDateTime.parse(p[7], DATE_FORMAT));
        return f;
    }
}
