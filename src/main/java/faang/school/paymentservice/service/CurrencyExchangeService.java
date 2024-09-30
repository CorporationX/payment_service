package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.Currency;

import java.math.BigDecimal;

public interface CurrencyExchangeService {
    BigDecimal convertCurrency(Currency fromCurrency, Currency toCurrency,
                               BigDecimal amount, String appId);
}