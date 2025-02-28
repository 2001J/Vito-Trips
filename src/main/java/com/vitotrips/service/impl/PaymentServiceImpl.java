package com.vitotrips.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.Refund;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.RefundCreateParams;
import com.vitotrips.DTO.PaymentRequest;
import com.vitotrips.DTO.PaymentResponse;
import com.vitotrips.model.Booking;
import com.vitotrips.model.Payment;
import com.vitotrips.repository.BookingRepository;
import com.vitotrips.repository.PaymentRepository;
import com.vitotrips.service.PaymentService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    @Value("${stripe.api.key}")
    private String stripeApiKey;

    private final PaymentRepository paymentRepository;
    private final BookingRepository bookingRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, BookingRepository bookingRepository) {
        this.paymentRepository = paymentRepository;
        this.bookingRepository = bookingRepository;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    public Payment savePayment(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Optional<Payment> getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId);
    }

    @Override
    public List<Payment> getPaymentsByBookingId(Long bookingId) {
        return paymentRepository.findByBooking_Id(bookingId);
    }

    @Override
    public void deletePayment(Long paymentId) {
        paymentRepository.deleteById(paymentId);
    }

    /**
     * Create a payment intent with Stripe and save payment record
     */
    @Override
    @Transactional
    public PaymentResponse createPayment(PaymentRequest paymentRequest) throws StripeException {
        // Validate booking exists
        Booking booking = bookingRepository.findById(paymentRequest.getBookingId())
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        // Create payment intent with Stripe
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setCurrency("usd")
                .setAmount(paymentRequest.getAmount().multiply(new BigDecimal("100")).longValue()) // Convert to cents
                .setDescription("Payment for booking #" + booking.getId())
                .putMetadata("bookingId", booking.getId().toString())
                .setReceiptEmail(booking.getUser().getEmail())
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // Create payment record
        Payment payment = Payment.builder()
                .booking(booking)
                .amount(paymentRequest.getAmount())
                .processorPaymentId(paymentIntent.getId())
                .paymentStatus(Payment.PaymentStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Payment savedPayment = paymentRepository.save(payment);

        // Return client secret for frontend to complete payment
        return PaymentResponse.builder()
                .paymentId(savedPayment.getId())
                .clientSecret(paymentIntent.getClientSecret())
                .status(savedPayment.getPaymentStatus().name())
                .build();
    }

    /**
     * Process a successful payment
     */
    @Override
    @Transactional
    public Payment processSuccessfulPayment(String paymentIntentId) throws StripeException {
        // Retrieve payment intent from Stripe
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

        // Find payment by processor payment ID
        Payment payment = paymentRepository.findByProcessorPaymentId(paymentIntentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        // Update payment status
        payment.setPaymentStatus(Payment.PaymentStatus.CONFIRMED);
        payment.setUpdatedAt(LocalDateTime.now());

        // Update booking payment status
        Booking booking = payment.getBooking();
        booking.setPaidAmount(booking.getPaidAmount().add(payment.getAmount()));
        
        // Check if booking is fully paid
        if (booking.getPaidAmount().compareTo(booking.getTotalAmount()) >= 0) {
            booking.setPaymentStatus(Booking.PaymentStatus.CONFIRMED);
        } else {
            booking.setPaymentStatus(Booking.PaymentStatus.PENDING);
        }
        
        booking.setUpdatedAt(LocalDateTime.now());
        
        bookingRepository.save(booking);
        return paymentRepository.save(payment);
    }

    /**
     * Process a failed payment
     */
    @Override
    @Transactional
    public Payment processFailedPayment(String paymentIntentId, String errorMessage) {
        // Find payment by processor payment ID
        Payment payment = paymentRepository.findByProcessorPaymentId(paymentIntentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        // Update payment status
        payment.setPaymentStatus(Payment.PaymentStatus.FAILED);
        payment.setUpdatedAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    /**
     * Refund a payment
     */
    @Override
    @Transactional
    public Payment refundPayment(Long paymentId) throws StripeException {
        // Find payment
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found"));

        // Check if payment can be refunded
        if (payment.getPaymentStatus() != Payment.PaymentStatus.CONFIRMED) {
            throw new IllegalStateException("Payment cannot be refunded because it is not confirmed");
        }

        // Create refund in Stripe
        RefundCreateParams params = RefundCreateParams.builder()
                .setPaymentIntent(payment.getProcessorPaymentId())
                .build();

        Refund refund = Refund.create(params);

        // Update payment status
        payment.setPaymentStatus(Payment.PaymentStatus.REFUNDED);
        payment.setUpdatedAt(LocalDateTime.now());

        // Update booking payment status
        Booking booking = payment.getBooking();
        booking.setPaidAmount(booking.getPaidAmount().subtract(payment.getAmount()));
        booking.setPaymentStatus(Booking.PaymentStatus.REFUNDED);
        booking.setUpdatedAt(LocalDateTime.now());
        
        bookingRepository.save(booking);
        return paymentRepository.save(payment);
    }
}