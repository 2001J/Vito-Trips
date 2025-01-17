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
@Table(name = "groups") // Explicit table name for clarity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Avoid recursive equals/hashCode
public class Group {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // Only include id in hashCode/equals
    private Long id;

    @Column(name = "group_name", nullable = false, unique = true) // Ensure constraints at the DB level
    @NotNull
    @Size(min = 1, max = 100, message = "Group name must be between 1 and 100 characters")
    private String groupName;

    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetching for leader
    @JoinColumn(name = "leader_id", nullable = false)
    private User leader; // Group leader

    @ManyToOne(fetch = FetchType.LAZY) // Lazy fetching for tour
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour; // Related tour for the group

    // Removed bookings (error-causing), no unnecessary relationships with Booking

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default // Ensure empty list upon creation
    private List<GroupMember> members = new ArrayList<>();

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
     * Excludes optional fields like `members`.
     */
    public Group(String groupName, User leader, Tour tour) {
        this.groupName = groupName;
        this.leader = leader;
        this.tour = tour;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Group{id=%d, groupName='%s', createdAt=%s}".formatted(id, groupName, createdAt);
    }
}