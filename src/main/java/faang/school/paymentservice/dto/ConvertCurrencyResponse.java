package faang.school.paymentservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@AllArgsConstructor
@Data
public class ConvertCurrencyResponse {
    private int verificationCode;
    private BigDecimal amount;
    private Currency currencyFrom;
    private BigDecimal convertedAmount;
    private Currency currencyTo;
    private String message;
}
