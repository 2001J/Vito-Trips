package com.vitotrips.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "reviews") // Explicitly specify the table name
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the review

    @Column(nullable = false)
    private String content; // The review text left by the user

    @Column(nullable = false)
    private int rating; // Rating provided by the user (e.g., 1-5 stars)

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // The user who wrote the review

    @ManyToOne
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour; // The tour the review is about
}