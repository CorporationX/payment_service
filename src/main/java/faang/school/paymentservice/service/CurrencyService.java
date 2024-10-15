package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.PaymentRequestDto;
import faang.school.paymentservice.model.Currency;
import faang.school.paymentservice.dto.response.CurrencyExchangeResponse;

import java.math.BigDecimal;

public interface CurrencyService {
    CurrencyExchangeResponse getCurrentCurrencyExchangeRate();
    BigDecimal convertWithCommission(PaymentRequestDto dto, Currency targetCurrency);
    String createMessage(PaymentRequestDto dto, BigDecimal newAmount, Currency targetCurrency);
}
