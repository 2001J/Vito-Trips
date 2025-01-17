package com.vitotrips.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tours") // Explicit table name for clarity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Avoid recursive equals/hashCode
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // Only include id in hashCode/equals
    private Long id;

    @Column(name = "tour_name", nullable = false, unique = true) // Ensure constraints at the DB level
    @NotNull
    @Size(min = 1, max = 100, message = "Tour name must be between 1 and 100 characters")
    private String tourName;

    @NotNull
    @Column(name = "location", nullable = false)
    private String location;

    @Column(name = "description")
    private String description;

    // Removed invalid bookings relationship (error-causing)

    @OneToMany(mappedBy = "tour", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Group> groups = new ArrayList<>();

    @CreatedDate // Automatically populated on create
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate // Automatically updated on modification
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Custom constructor for required fields.
     */
    public Tour(String tourName, String location) {
        this.tourName = tourName;
        this.location = location;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Tour{id=%d, tourName='%s', location='%s', createdAt=%s}".formatted(
                id, tourName, location, createdAt
        );
    }
}