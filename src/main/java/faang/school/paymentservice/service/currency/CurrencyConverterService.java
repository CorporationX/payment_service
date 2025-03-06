package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.dto.Currency;

import java.math.BigDecimal;

public interface CurrencyConverterService {
    BigDecimal convertCurrency(Currency from, Currency to, BigDecimal amount);
}
