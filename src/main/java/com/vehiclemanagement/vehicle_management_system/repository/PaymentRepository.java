package com.vehiclemanagement.vehicle_management_system.repository;

import com.vehiclemanagement.vehicle_management_system.model.Payment;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepository extends FileRepository<Payment> {
    public PaymentRepository() {
        super("payments.txt", Payment::fromLine);
    }
}
