package com.vehiclemanagement.vehicle_management_system.repository;

import com.vehiclemanagement.vehicle_management_system.model.Feedback;
import org.springframework.stereotype.Repository;

@Repository
public class FeedbackRepository extends FileRepository<Feedback> {
    public FeedbackRepository() {
        super("feedback.txt", Feedback::fromLine);
    }
}
