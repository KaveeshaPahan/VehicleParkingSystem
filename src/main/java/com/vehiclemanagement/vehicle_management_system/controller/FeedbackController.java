package com.vehiclemanagement.vehicle_management_system.controller;

import com.vehiclemanagement.vehicle_management_system.model.Feedback;
import com.vehiclemanagement.vehicle_management_system.service.FeedbackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*") // Allows frontend applications from any origin to access APIs
public class FeedbackController {

    private final FeedbackService feedbackService;

    // Service layer object used to perform business logic operations
    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping
    public List<Feedback> all(@RequestParam(value = "q", required = false) String q,
                              @RequestParam(value = "userId", required = false) String userId) {
        // Return feedback belonging to a specific user
        if (userId != null && !userId.isBlank()) return feedbackService.getByUser(userId);
        // Return all feedback or filtered search results
        return q == null ? feedbackService.getAll() : feedbackService.search(q);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feedback> byId(@PathVariable String id) {
        return feedbackService.getById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Feedback> create(@RequestBody Feedback f) {

        // Validation check for empty subject or message
        if (f.getSubject() == null || f.getSubject().isBlank()
                || f.getMessage() == null || f.getMessage().isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        // Save feedback to database
        return ResponseEntity.ok(feedbackService.create(f));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Feedback> update(@PathVariable String id, @RequestBody Feedback f) {
        return feedbackService.update(id, f)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String id) {
        // Check whether deletion was successful
        if (!feedbackService.delete(id)) return ResponseEntity.notFound().build();
        // Return success response with deleted ID
        return ResponseEntity.ok(Map.of("deleted", true, "id", id));
    }
}
