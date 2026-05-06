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

    public Optional<User> getById(String id) {
        return userRepository.findById(id);
    }

    public List<User> search(String query) {
        if (query == null || query.isBlank()) return getAll();
        String q = query.toLowerCase();
        return userRepository.findAll().stream().filter(u ->
                (u.getName() != null && u.getName().toLowerCase().contains(q)) ||
                (u.getEmail() != null && u.getEmail().toLowerCase().contains(q)) ||
                (u.getPhone() != null && u.getPhone().toLowerCase().contains(q)) ||
                (u.getRole() != null && u.getRole().toLowerCase().contains(q))
        ).toList();
    }

    public User create(User user) {
        if (user.getRole() == null || user.getRole().isBlank()) user.setRole("CUSTOMER");
        return userRepository.save(user);
    }

    public Optional<User> update(String id, User user) {
        user.setId(id);
        return userRepository.update(user);
    }

    public boolean delete(String id) {
        return userRepository.deleteById(id);
    }
}
