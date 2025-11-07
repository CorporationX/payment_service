package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import org.springframework.http.ResponseEntity;

public interface PaymentService {
    ResponseEntity<PaymentResponse> sendPayment(PaymentRequest paymentRequest);
}
