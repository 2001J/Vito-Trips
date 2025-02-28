package com.vitotrips.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for payment response
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long paymentId;
    
    private String clientSecret;
    
    private String status;
    
    private String message;
    
    private String redirectUrl;
} 