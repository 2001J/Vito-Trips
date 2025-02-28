package com.vitotrips.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true) // Safer equals/hashCode
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // Optimize performance
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    @Column(name = "amount", precision = 10, scale = 2, nullable = false)
    @NotNull
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "processor_payment_id")
    private String processorPaymentId;

    @Column(name = "processor_fee", precision = 10, scale = 2)
    private BigDecimal processorFee;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "payment_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime paymentDate;

    @Column(name = "refund_date")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime refundDate;

    @Column(name = "refund_amount", precision = 10, scale = 2)
    private BigDecimal refundAmount;

    @Column(name = "refund_reason")
    private String refundReason;

    @CreatedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Payment method options like CREDIT_CARD, PAYPAL, etc.
     */
    public enum PaymentMethod {
        CREDIT_CARD, PAYPAL, BANK_TRANSFER
    }

    /**
     * Payment status options like PENDING, SUCCESS, etc.
     */
    public enum PaymentStatus {
        PENDING, CONFIRMED, FAILED, REFUNDED
    }
}