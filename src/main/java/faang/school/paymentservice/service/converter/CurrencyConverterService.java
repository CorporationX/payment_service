package faang.school.paymentservice.service.converter;

import faang.school.paymentservice.dto.Currency;

import java.math.BigDecimal;

public interface CurrencyConverterService {

    BigDecimal convert(Currency fromCurrency, Currency toCurrency, BigDecimal amount);
}
