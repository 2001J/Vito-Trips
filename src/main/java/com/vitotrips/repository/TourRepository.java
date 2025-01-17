package com.vitotrips.repository;

import com.vitotrips.model.Tour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourRepository extends JpaRepository<Tour, Long> {

    // Find tours based on the location
    List<Tour> findByLocation(String location);

    // Optional: Fetch based on keywords in the name
    List<Tour> findByTourNameContaining(String name); // Example for searching by name

    // Removed error-causing method: List<Tour> findByBookings_Group_Id(Long groupId);
}