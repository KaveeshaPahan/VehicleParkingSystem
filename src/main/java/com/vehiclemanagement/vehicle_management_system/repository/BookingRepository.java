package com.vehiclemanagement.vehicle_management_system.repository;

import com.vehiclemanagement.vehicle_management_system.model.Booking;
import org.springframework.stereotype.Repository;

@Repository
public class BookingRepository extends FileRepository<Booking> {
    public BookingRepository() {
        super("bookings.txt", Booking::fromLine);
    }
}
