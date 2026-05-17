package com.vehiclemanagement.vehicle_management_system.service;

import com.vehiclemanagement.vehicle_management_system.model.User;
import com.vehiclemanagement.vehicle_management_system.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    // Finds and returns a specific user wrapped in an Optional by their unique ID
    public Optional<User> getById(String id) {
        return userRepository.findById(id);
    }

    // Searches through all users for a keyword matching their name, email, phone, or role
    public List<User> search(String query) {
        if (query == null || query.isBlank()) return getAll();
        String q = query.toLowerCase();

        // Filter users based on whether any field contains the search text
        return userRepository.findAll().stream().filter(u ->
                (u.getName() != null && u.getName().toLowerCase().contains(q)) ||
                (u.getEmail() != null && u.getEmail().toLowerCase().contains(q)) ||
                (u.getPhone() != null && u.getPhone().toLowerCase().contains(q)) ||
                (u.getRole() != null && u.getRole().toLowerCase().contains(q))
        ).toList();
    }

    // Saves a new user and sets their default role to "CUSTOMER" if none is provided
    public User create(User user) {
        if (user.getRole() == null || user.getRole().isBlank()) user.setRole("CUSTOMER");
        return userRepository.save(user);
    }

    // Assigns the target ID to the user object and updates their details in the data storage
    public Optional<User> update(String id, User user) {
        user.setId(id);
        return userRepository.update(user);
    }

    // Deletes a user by their ID and returns true if successful, false otherwise
    public boolean delete(String id) {
        return userRepository.deleteById(id);
    }
}