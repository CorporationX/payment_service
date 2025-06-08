package faang.school.paymentservice.service.currency.conversion;

import faang.school.paymentservice.dto.PaymentRequest;

import java.math.BigDecimal;

public interface CurrencyConversionService {
    BigDecimal getConvertedSum(PaymentRequest dto);
}
