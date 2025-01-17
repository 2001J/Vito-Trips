package com.vitotrips.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id", nullable = false)
    private Tour tour;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = true)
    private Group group;

    @Column(name = "total_amount", precision = 10, scale = 2, nullable = false)
    @NotNull
    private BigDecimal totalAmount;

    @Column(name = "paid_amount", precision = 10, scale = 2, nullable = false)
    @NotNull
    private BigDecimal paidAmount;

    @Column(name = "installment_plan", nullable = false)
    private boolean installmentPlan;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "booking_date", nullable = false)
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime bookingDate; // Added field

    @Column(name = "special_instructions", length = 500) // Optional additional field
    private String specialInstructions;

    @Enumerated(EnumType.STRING) // Optional enum for booking types
    @Column(name = "booking_type", nullable = false)
    private BookingType bookingType;

    @CreatedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", tour=" + (tour != null ? tour.getId() : "null") +
                ", user=" + (user != null ? user.getId() : "null") +
                ", group=" + (group != null ? group.getId() : "null") +
                ", totalAmount=" + totalAmount +
                ", paidAmount=" + paidAmount +
                ", installmentPlan=" + installmentPlan +
                ", paymentStatus=" + paymentStatus +
                ", bookingDate=" + bookingDate +
                ", specialInstructions='" + specialInstructions + '\'' +
                ", bookingType=" + bookingType +
                '}';
    }

    public enum PaymentStatus {
        PENDING, CONFIRMED, FAILED, REFUNDED
    }

    public enum BookingType { // Optional
        INDIVIDUAL, GROUP, CORPORATE
    }
}