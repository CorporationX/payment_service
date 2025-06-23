package faang.school.paymentservice.service.currency.conversion;

import faang.school.paymentservice.dto.PaymentRequest;

import java.math.BigDecimal;
import java.util.Map;

public interface CurrencyConversionService {
    Map<String, BigDecimal> getExchangeRates();

    void updateRates(Map<String, BigDecimal> newRates);

    BigDecimal getConvertedSum(PaymentRequest dto);
}
