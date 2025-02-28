package com.vitotrips.controller;

import com.vitotrips.model.Tour;
import com.vitotrips.service.TourService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tours")
public class TourController {

    private final TourService tourService;

    public TourController(TourService tourService) {
        this.tourService = tourService;
    }

    @PostMapping
    public ResponseEntity<Tour> saveTour(@RequestBody Tour tour) {
        Tour savedTour = tourService.saveTour(tour);
        return ResponseEntity.ok(savedTour);
    }

    @GetMapping
    public ResponseEntity<List<Tour>> getAllTours() {
        List<Tour> tours = tourService.getAllTours();
        return ResponseEntity.ok(tours);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tour> getTourById(@PathVariable Long id) {
        return tourService.getTourById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<Tour>> getToursByLocation(@PathVariable String location) {
        List<Tour> tours = tourService.getToursByLocation(location);
        return ResponseEntity.ok(tours);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTour(@PathVariable Long id) {
        tourService.deleteTour(id);
        return ResponseEntity.noContent().build();
    }
}