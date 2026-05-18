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

    public Feedback() { super(); }

    public Feedback(String userId, String subject, String message, int rating, String status) {
        super();
        this.userId = userId;
        this.subject = subject;
        this.message = message;
        this.rating = rating;
        this.status = status;
    }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

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

    public static Feedback fromLine(String line) {
        String[] p = line.split(DELIM, -1);
        if (p.length < 8) return null;
        Feedback f = new Feedback();
        f.setId(p[0]);
        f.setUserId(p[1]);
        f.setSubject(p[2]);
        f.setMessage(p[3]);
        try { f.setRating(Integer.parseInt(p[4])); } catch (NumberFormatException e) { f.setRating(0); }
        f.setStatus(p[5]);
        f.setCreatedAt(LocalDateTime.parse(p[6], DATE_FORMAT));
        f.setUpdatedAt(LocalDateTime.parse(p[7], DATE_FORMAT));
        return f;
    }
}
