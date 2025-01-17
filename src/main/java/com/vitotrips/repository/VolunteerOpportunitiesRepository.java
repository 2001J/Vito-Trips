package com.vitotrips.repository;

import com.vitotrips.model.VolunteerOpportunities;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VolunteerOpportunitiesRepository extends JpaRepository<VolunteerOpportunities, Long> {

    // Find opportunities by associated tour
    List<VolunteerOpportunities> findByTour_Id(Long tourId);

    // Find opportunities within a specific date range
    List<VolunteerOpportunities> findByStartDateBetween(LocalDateTime start, LocalDateTime end);
}