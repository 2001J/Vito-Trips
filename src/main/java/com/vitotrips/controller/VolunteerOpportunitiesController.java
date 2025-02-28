package com.vitotrips.controller;

import com.vitotrips.model.VolunteerOpportunities;
import com.vitotrips.service.VolunteerOpportunitiesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/volunteer-opportunities")
public class VolunteerOpportunitiesController {

    private final VolunteerOpportunitiesService service;

    public VolunteerOpportunitiesController(VolunteerOpportunitiesService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<VolunteerOpportunities> saveOpportunity(@RequestBody VolunteerOpportunities opportunity) {
        VolunteerOpportunities savedOpportunity = service.saveOpportunity(opportunity);
        return ResponseEntity.ok(savedOpportunity);
    }

    @GetMapping
    public ResponseEntity<List<VolunteerOpportunities>> getAllOpportunities() {
        List<VolunteerOpportunities> opportunities = service.getAllOpportunities();
        return ResponseEntity.ok(opportunities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<VolunteerOpportunities> getOpportunityById(@PathVariable Long id) {
        return service.getOpportunityById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tour/{tourId}")
    public ResponseEntity<List<VolunteerOpportunities>> getOpportunitiesByTourId(@PathVariable Long tourId) {
        List<VolunteerOpportunities> opportunities = service.getOpportunitiesByTourId(tourId);
        return ResponseEntity.ok(opportunities);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<VolunteerOpportunities>> getOpportunitiesByDateRange(
            @RequestParam LocalDateTime startDate,
            @RequestParam LocalDateTime endDate
    ) {
        List<VolunteerOpportunities> opportunities = service.getOpportunitiesByDateRange(startDate, endDate);
        return ResponseEntity.ok(opportunities);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOpportunity(@PathVariable Long id) {
        service.deleteOpportunity(id);
        return ResponseEntity.noContent().build();
    }
}