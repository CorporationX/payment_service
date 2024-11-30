package faang.school.paymentservice.validator;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.exception.DataValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class CurrencyValidator {

    @Value("${currency-api.minimum-amount}")
    private BigDecimal minimumAmount;

    @Value("${currency-api.baseCurrency}")
    private Currency baseCurrency;

    public boolean validateMinimumTransferAmount(BigDecimal amountAfterConvert) {
        if (amountAfterConvert.compareTo(minimumAmount) < 0) {
            log.error("Payment attempts less than {}", minimumAmount);
            throw new DataValidationException("The payment cannot be less than " + minimumAmount + baseCurrency.name());
        }
        return true;
    }
}
