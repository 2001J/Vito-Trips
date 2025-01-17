package com.vitotrips.repository;

import com.vitotrips.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    // Existing methods (retained)
    List<Booking> findByTourId(Long tourId); // Find bookings by tour ID
    List<Booking> findByUserId(Long userId); // Find bookings by user ID

    // New methods (enhanced and general-purpose queries)

    // Find bookings by booking type (e.g., INDIVIDUAL, GROUP)
    List<Booking> findByBookingType(Booking.BookingType bookingType);

    // Find bookings within a specific date range (e.g., analytics, reporting)
    List<Booking> findByBookingDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    // Find bookings by group ID (useful for group-oriented features)
    List<Booking> findByGroup_Id(Long groupId);

    // Find bookings by payment status (e.g., PENDING, CONFIRMED, CANCELLED)
    List<Booking> findByPaymentStatus(Booking.PaymentStatus paymentStatus);

    // Find bookings with a specific payment status for a user
    List<Booking> findByUserIdAndPaymentStatus(Long userId, Booking.PaymentStatus paymentStatus);

    // Optional: Combine filters for complex use cases
    // Example: Filter bookings by tour, user, and a date range
    List<Booking> findByTourIdAndUserIdAndBookingDateBetween(
            Long tourId,
            Long userId,
            LocalDateTime startDate,
            LocalDateTime endDate
    );

    // Optional: Fetch bookings for a specific tour with payment status
    List<Booking> findByTourIdAndPaymentStatus(Long tourId, Booking.PaymentStatus paymentStatus);
}