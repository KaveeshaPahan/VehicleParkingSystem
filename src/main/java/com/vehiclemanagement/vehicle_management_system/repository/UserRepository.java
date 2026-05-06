package com.vehiclemanagement.vehicle_management_system.repository;

import com.vehiclemanagement.vehicle_management_system.model.User;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepository extends FileRepository<User> {
    public UserRepository() {
        super("users.txt", User::fromLine);
    }
}
