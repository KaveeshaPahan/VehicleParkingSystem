package com.vehiclemanagement.vehicle_management_system.repository;

import com.vehiclemanagement.vehicle_management_system.model.Payment;
import org.springframework.stereotype.Repository;

// Indicates that this class handles data access and storage operations for payments
@Repository
public class PaymentRepository extends FileRepository<Payment> {

    // Configures the repository to use a specific text file and the custom deserializer function
    public PaymentRepository() {
        super("payments.txt", Payment::fromLine);
    }
}
