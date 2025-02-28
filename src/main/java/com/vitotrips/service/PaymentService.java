package com.vitotrips.service;

import com.stripe.exception.StripeException;
import com.vitotrips.DTO.PaymentRequest;
import com.vitotrips.DTO.PaymentResponse;
import com.vitotrips.model.Payment;

import java.util.List;
import java.util.Optional;

public interface PaymentService {
    
    Payment savePayment(Payment payment);
    
    List<Payment> getAllPayments();
    
    Optional<Payment> getPaymentById(Long paymentId);
    
    List<Payment> getPaymentsByBookingId(Long bookingId);
    
    void deletePayment(Long paymentId);
    
    // Add methods from the current PaymentService implementation
    PaymentResponse createPayment(PaymentRequest paymentRequest) throws StripeException;
    
    Payment processSuccessfulPayment(String paymentIntentId) throws StripeException;
    
    Payment processFailedPayment(String paymentIntentId, String errorMessage);
    
    Payment refundPayment(Long paymentId) throws StripeException;
} 