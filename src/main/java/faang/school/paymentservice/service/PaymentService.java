package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;

import java.math.BigDecimal;

public interface PaymentService {
    PaymentResponse sendPayment(PaymentRequest dto);

    BigDecimal convertCurrency(BigDecimal amount, Currency fromCurrency, Currency toCurrency);
}