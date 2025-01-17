package com.vitotrips.service.impl;

import com.vitotrips.model.Tour;
import com.vitotrips.repository.TourRepository;
import com.vitotrips.service.TourService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TourServiceImpl implements TourService {

    private final TourRepository tourRepository;

    public TourServiceImpl(TourRepository tourRepository) {
        this.tourRepository = tourRepository;
    }

    @Override
    public Tour saveTour(Tour tour) {
        return tourRepository.save(tour);
    }

    @Override
    public List<Tour> getAllTours() {
        return tourRepository.findAll();
    }

    @Override
    public Optional<Tour> getTourById(Long tourId) {
        return tourRepository.findById(tourId);
    }

    @Override
    public List<Tour> getToursByLocation(String location) {
        return tourRepository.findByLocation(location);
    }

    @Override
    public void deleteTour(Long tourId) {
        tourRepository.deleteById(tourId);
    }
}