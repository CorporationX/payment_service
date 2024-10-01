package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;

import java.math.BigDecimal;

public interface CurrencyExchangeService {
    BigDecimal convertCurrency(Currency fromCurrency, Currency toCurrency,
                               BigDecimal amount, String appId);

    String getMessage(PaymentRequest dto, Currency toCurrency);
}