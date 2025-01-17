package com.vitotrips.service;

import com.vitotrips.model.Tour;

import java.util.List;
import java.util.Optional;

public interface TourService {
    Tour saveTour(Tour tour);
    List<Tour> getAllTours();
    Optional<Tour> getTourById(Long tourId);
    List<Tour> getToursByLocation(String location);
    void deleteTour(Long tourId);
}