package com.jethjoe.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "operator_id")
    private User operator;

    private String title;

    private String description;

    private String destination;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private BigDecimal pricePerPerson;

    private int maxGroupSize;

    private boolean volunteerOpportunities;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;


}
