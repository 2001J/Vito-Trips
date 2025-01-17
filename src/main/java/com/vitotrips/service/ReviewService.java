package com.vitotrips.service;

import com.vitotrips.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewService {
    Review saveReview(Review review);
    List<Review> getAllReviews();
    List<Review> getReviewsByTourId(Long tourId);
    Optional<Review> getReviewById(Long reviewId);
    void deleteReview(Long reviewId);
}