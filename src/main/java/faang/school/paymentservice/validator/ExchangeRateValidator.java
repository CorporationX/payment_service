package faang.school.paymentservice.validator;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRateResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Slf4j
@Component
public class ExchangeRateValidator {

    public void validationExchangeRateResponse(ExchangeRateResponseDto responseDto) {
        if (responseDto == null || responseDto.getRates() == null) {
            log.error("ResponseDto or rates is null!");
            throw new RuntimeException("Failed to retrieve exchange rates");
        }
    }

    public void validationExchangeRateAmount(BigDecimal exchangeRate, Currency toCurrency) {
        if (exchangeRate == null) {
            log.error("ExchangeRate is null!");
            throw new IllegalArgumentException("Exchange rate not found for currency: " + toCurrency.name());
        }
    }
}
