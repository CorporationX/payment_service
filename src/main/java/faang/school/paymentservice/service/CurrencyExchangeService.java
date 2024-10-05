package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;

import java.math.BigDecimal;

public interface CurrencyExchangeService {
    BigDecimal convertWithCommission(PaymentRequest dto, Currency toCurrency);
}