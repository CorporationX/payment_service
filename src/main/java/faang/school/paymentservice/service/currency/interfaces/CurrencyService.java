package faang.school.paymentservice.service.currency.interfaces;

import faang.school.paymentservice.dto.Currency;

import java.math.BigDecimal;
import java.util.Map;

public interface CurrencyService {
    Map<Currency, BigDecimal> fetchExchangeRates();

    Map<Currency, BigDecimal> getExchangeRates();

    Currency getBaseCurrency();
}
