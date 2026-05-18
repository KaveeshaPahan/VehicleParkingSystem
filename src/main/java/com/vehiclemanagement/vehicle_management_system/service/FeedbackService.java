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

    public FeedbackService(FeedbackRepository repo) {
        this.repo = repo;
    }


    public List<Feedback> getAll() { return repo.findAll(); }

    public Optional<Feedback> getById(String id) { return repo.findById(id); }

    public List<Feedback> getByUser(String userId) {
        return repo.findAll().stream()
                .filter(f -> userId.equals(f.getUserId())).toList();
    }

    public List<Feedback> search(String query) {
        if (query == null || query.isBlank()) return getAll();
        String q = query.toLowerCase();
        return repo.findAll().stream().filter(f ->
                (f.getSubject() != null && f.getSubject().toLowerCase().contains(q)) ||
                (f.getMessage() != null && f.getMessage().toLowerCase().contains(q)) ||
                (f.getStatus() != null && f.getStatus().toLowerCase().contains(q))
        ).toList();
    }

    public Feedback create(Feedback feedback) {
        if (feedback.getStatus() == null || feedback.getStatus().isBlank()) feedback.setStatus("OPEN");
        return repo.save(feedback);
    }

    public Optional<Feedback> update(String id, Feedback feedback) {
        feedback.setId(id);
        return repo.update(feedback);
    }

    public boolean delete(String id) { return repo.deleteById(id); }
}
