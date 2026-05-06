package com.vehiclemanagement.vehicle_management_system.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Abstract base class for all domain entities.
 * Demonstrates: ABSTRACTION (cannot be instantiated), ENCAPSULATION (private fields),
 * INHERITANCE (subclasses extend this), and provides a polymorphic toLine()/fromLine()
 * contract used by the generic file repository.
 */
public abstract class BaseEntity {

    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    protected static final String DELIM = "\\|";
    protected static final String SEP = "|";

    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    protected BaseEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public void touch() { this.updatedAt = LocalDateTime.now(); }

    /** Polymorphic serialization: each subclass writes its fields as a single line. */
    public abstract String toLine();

    protected static String safe(String value) {
        if (value == null) return "";
        return value.replace("|", "/").replace("\n", " ").replace("\r", " ");
    }
}
