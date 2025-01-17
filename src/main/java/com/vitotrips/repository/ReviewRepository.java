package com.vitotrips.repository;

import com.vitotrips.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByTourId(Long tourId); // Get reviews for a specific tour
    List<Review> findByUserId(Long userId); // Get reviews by a specific user
}