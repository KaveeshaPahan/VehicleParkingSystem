package com.vehiclemanagement.vehicle_management_system.controller;

import com.vehiclemanagement.vehicle_management_system.model.User;
import com.vehiclemanagement.vehicle_management_system.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController  // Marks this class as a REST API controller
@RequestMapping("/api/auth")  // Base URL for all authentication-related endpoints
@CrossOrigin(origins = "*") // Allows requests from any frontend/domain
public class AuthController {

    // Service layer object used to handle user-related operations
    private final UserService userService;

    // Constructor Injection for UserService
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // API endpoint for user login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {

        // Get email and password from request body
        String email = body.getOrDefault("email", "").trim();
        String password = body.getOrDefault("password", "");
        if (email.isEmpty() || password.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email and password are required"));
        }
        Optional<User> match = userService.getAll().stream()
                .filter(u -> email.equalsIgnoreCase(u.getEmail()) && password.equals(u.getPassword()))
                .findFirst();
        if (match.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password"));
        }
        User u = match.get();
        // never send password back to the client
        u.setPassword(null);
        return ResponseEntity.ok(Map.of("user", u));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()
                || user.getEmail() == null || user.getEmail().isBlank()
                || user.getPassword() == null || user.getPassword().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Name, email and password are required"));
        }
        boolean exists = userService.getAll().stream()
                .anyMatch(u -> user.getEmail().equalsIgnoreCase(u.getEmail()));
        if (exists) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "An account with this email already exists"));
        }
        if (user.getRole() == null || user.getRole().isBlank()) user.setRole("CUSTOMER");
        User saved = userService.create(user);
        saved.setPassword(null);
        return ResponseEntity.ok(Map.of("user", saved));
    }
}
