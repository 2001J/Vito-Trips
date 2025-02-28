package com.vitotrips.controller;

import com.stripe.exception.StripeException;
import com.vitotrips.DTO.PaymentRequest;
import com.vitotrips.DTO.PaymentResponse;
import com.vitotrips.model.Payment;
import com.vitotrips.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> createPayment(@Valid @RequestBody PaymentRequest paymentRequest) {
        try {
            PaymentResponse response = paymentService.createPayment(paymentRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (StripeException e) {
            PaymentResponse errorResponse = PaymentResponse.builder()
                    .status("FAILED")
                    .message("Payment processing failed: " + e.getMessage())
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPayment(@PathVariable Long id) {
        return paymentService.getPaymentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/booking/{bookingId}")
    public ResponseEntity<?> getPaymentsByBookingId(@PathVariable Long bookingId) {
        return ResponseEntity.ok(paymentService.getPaymentsByBookingId(bookingId));
    }

    @PostMapping("/webhook")
    public ResponseEntity<?> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String signature) {
        try {
            // Process the webhook event
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/{id}/refund")
    public ResponseEntity<?> refundPayment(@PathVariable Long id) {
        try {
            Payment refundedPayment = paymentService.refundPayment(id);
            return ResponseEntity.ok(refundedPayment);
        } catch (StripeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Refund failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
}