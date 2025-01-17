package com.vitotrips.service.impl;

import com.vitotrips.model.VolunteerOpportunities;
import com.vitotrips.repository.VolunteerOpportunitiesRepository;
import com.vitotrips.service.VolunteerOpportunitiesService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class VolunteerOpportunitiesServiceImpl implements VolunteerOpportunitiesService {

    private final VolunteerOpportunitiesRepository repository;

    public VolunteerOpportunitiesServiceImpl(VolunteerOpportunitiesRepository repository) {
        this.repository = repository;
    }

    @Override
    public VolunteerOpportunities saveOpportunity(VolunteerOpportunities opportunity) {
        return repository.save(opportunity);
    }

    @Override
    public List<VolunteerOpportunities> getAllOpportunities() {
        return repository.findAll();
    }

    @Override
    public Optional<VolunteerOpportunities> getOpportunityById(Long id) {
        return repository.findById(id);
    }

    @Override
    public List<VolunteerOpportunities> getOpportunitiesByTourId(Long tourId) {
        return repository.findByTour_Id(tourId);
    }

    @Override
    public List<VolunteerOpportunities> getOpportunitiesByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return repository.findByStartDateBetween(startDate, endDate);
    }

    @Override
    public void deleteOpportunity(Long id) {
        repository.deleteById(id);
    }
}