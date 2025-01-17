package com.vitotrips.service;

import com.vitotrips.model.VolunteerOpportunities;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VolunteerOpportunitiesService {

    VolunteerOpportunities saveOpportunity(VolunteerOpportunities opportunity);

    List<VolunteerOpportunities> getAllOpportunities();

    Optional<VolunteerOpportunities> getOpportunityById(Long id);

    List<VolunteerOpportunities> getOpportunitiesByTourId(Long tourId);

    List<VolunteerOpportunities> getOpportunitiesByDateRange(LocalDateTime startDate, LocalDateTime endDate);

    void deleteOpportunity(Long id);
}