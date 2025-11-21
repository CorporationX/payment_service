package faang.school.paymentservice.service;

import java.math.BigDecimal;

public interface CurrencyConverterService {
    BigDecimal convertToTargetCurrency(String fromCurrency, BigDecimal amount);
}
