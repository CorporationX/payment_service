package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.PaymentRequestDto;
import faang.school.paymentservice.dto.PaymentResponseDto;
import faang.school.paymentservice.dto.exchange.ExchangeRequestDto;
import faang.school.paymentservice.dto.exchange.ExchangeResponseDto;
import org.springframework.http.ResponseEntity;

public interface PaymentService {
    ResponseEntity<PaymentResponseDto> sendPayment(PaymentRequestDto paymentRequestDto);

    ExchangeResponseDto convertCurrency(ExchangeRequestDto exchangeRequest);
}
