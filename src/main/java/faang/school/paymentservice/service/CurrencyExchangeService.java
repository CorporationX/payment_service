package faang.school.paymentservice.service;

import java.math.BigDecimal;

public interface CurrencyExchangeService {
    BigDecimal convertCurrency(String fromCurrency, String toCurrency,
                               BigDecimal amount, String appId);
}