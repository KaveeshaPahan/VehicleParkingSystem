package com.vehiclemanagement.vehicle_management_system.controller;

import com.vehiclemanagement.vehicle_management_system.model.User;
import com.vehiclemanagement.vehicle_management_system.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> all(@RequestParam(value = "q", required = false) String q) {
        return q == null ? userService.getAll() : userService.search(q);
    }

    // GET: Find a single user by their unique ID
    @GetMapping("/{id}")
    public ResponseEntity<User> byId(@PathVariable String id) {
        return userService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST: Create a new user after checking for valid data
    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()
                || user.getEmail() == null || user.getEmail().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(userService.create(user));
    }

    // PUT: Update details of an existing user by their ID
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable String id, @RequestBody User user) {
        return userService.update(id, user)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE: Remove a user from the system by their ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String id) {
        boolean ok = userService.delete(id);
        if (!ok) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(Map.of("deleted", true, "id", id));
    }
}