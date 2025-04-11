package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.PaymentRequestDto;
import faang.school.paymentservice.dto.PaymentResponseDto;
import org.springframework.http.ResponseEntity;

public interface PaymentService {
    ResponseEntity<PaymentResponseDto> sendPayment(PaymentRequestDto paymentRequestDto);
}
