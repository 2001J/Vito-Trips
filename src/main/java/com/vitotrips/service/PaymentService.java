package com.vitotrips.service;

import com.vitotrips.model.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentService {
    Payment savePayment(Payment payment);
    List<Payment> getAllPayments();
    Optional<Payment> getPaymentById(Long paymentId);
    List<Payment> getPaymentsByBookingId(Long bookingId);
    void deletePayment(Long paymentId);
}