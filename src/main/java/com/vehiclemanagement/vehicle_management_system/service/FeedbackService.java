package com.vehiclemanagement.vehicle_management_system.service;

import com.vehiclemanagement.vehicle_management_system.model.Feedback;
import com.vehiclemanagement.vehicle_management_system.repository.FeedbackRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

// Service class responsible for handling feedback business logic
@Service
public class FeedbackService {

    // Repository object used for feedback data operations
    private final FeedbackRepository repo;

    // Constructor injection for FeedbackRepository
    public FeedbackService(FeedbackRepository repo) {
        this.repo = repo;
    }

    // Returns all feedback records
    public List<Feedback> getAll() { return repo.findAll(); }

    // Returns a feedback record using its ID
    public Optional<Feedback> getById(String id) { return repo.findById(id); }


    public List<Feedback> getByUser(String userId) {
        return repo.findAll().stream()
                .filter(f -> userId.equals(f.getUserId())).toList();
    }
    // Returns feedback records belonging to a specific user
    public List<Feedback> search(String query) {
        if (query == null || query.isBlank()) return getAll();
        String q = query.toLowerCase();
        return repo.findAll().stream().filter(f ->
                (f.getSubject() != null && f.getSubject().toLowerCase().contains(q)) ||
                (f.getMessage() != null && f.getMessage().toLowerCase().contains(q)) ||
                (f.getStatus() != null && f.getStatus().toLowerCase().contains(q))
        ).toList();
    }
    // Creates and saves a new feedback record
    public Feedback create(Feedback feedback) {
        // Set default status if no status is provided
        if (feedback.getStatus() == null || feedback.getStatus().isBlank()) feedback.setStatus("OPEN");
        return repo.save(feedback);
    }
    // Updates an existing feedback record
    public Optional<Feedback> update(String id, Feedback feedback) {
        // Set feedback ID before updating
        feedback.setId(id);
        return repo.update(feedback);
    }

    public boolean delete(String id) { return repo.deleteById(id); }
}
