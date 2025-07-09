package faang.school.paymentservice.service.currency.conversion;

import faang.school.paymentservice.dto.PaymentRequest;

import java.math.BigDecimal;

public interface CurrencyConversionService {
    void getExchangeRates();

    BigDecimal getConvertedSum(PaymentRequest dto);
}
