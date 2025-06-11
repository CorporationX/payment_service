package faang.school.paymentservice.service;

import java.math.BigDecimal;

import faang.school.paymentservice.dto.Currency;

public interface CurrencyExchangeService {
    BigDecimal exchange(BigDecimal amount, Currency currency);
}
