package com.vitotrips.repository;

import com.vitotrips.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByBookingId(Long bookingId);

    List<Payment> findByBooking_Id(Long bookingId);

    Optional<Payment> findByProcessorPaymentId(String processorPaymentId);
}