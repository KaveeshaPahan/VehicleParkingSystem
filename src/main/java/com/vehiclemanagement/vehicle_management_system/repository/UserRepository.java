package com.vehiclemanagement.vehicle_management_system.repository;

import com.vehiclemanagement.vehicle_management_system.model.User;
import org.springframework.stereotype.Repository;

@Repository // Marks this class as a Spring component that handles data storage

// Constructor to configure the repository settings
public class UserRepository extends FileRepository<User> {
    public UserRepository() {
        super("users.txt", User::fromLine);
    }
}