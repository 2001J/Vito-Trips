package com.vitotrips.repository;

import com.vitotrips.model.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface GroupRepository extends JpaRepository<Group, Long> {

    // Find groups based on the group leader (user)
    List<Group> findByLeader_Id(Long userId);

    // Removed error-causing: List<Group> findByBookings_Id(Long bookingId);

    // Optional: Fetch based on specific group attributes
    List<Group> findByGroupNameContaining(String name); // Example for searching by name
}