package faang.school.paymentservice.dto;

import java.math.BigDecimal;

public record ExchangeCurrencyResponse(
        int verificationCode,
        BigDecimal amount,
        Currency currencyFrom,
        BigDecimal convertedAmount,
        Currency currencyTo,
        String message) {
}
