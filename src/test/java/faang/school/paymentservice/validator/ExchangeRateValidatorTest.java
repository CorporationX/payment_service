package faang.school.paymentservice.validator;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.ExchangeRateResponseDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ExchangeRateValidatorTest {

    @InjectMocks
    private ExchangeRateValidator exchangeRateValidator;

    private static final String DISCLAIMER = "disclaimer";
    private static final String LICENSE = "license";
    private static final long TIMESTAMP = 123456789L;
    private static final String BASE = "base";
    private static final Currency CURRENCY = Currency.USD;
    private static final Double EXCHANGE_RATE = 97.2;
    private Map<String, Double> rates = new HashMap<>();
    private ExchangeRateResponseDto responseDto;


    @BeforeEach
    public void init() {
        rates.put(CURRENCY.name(), EXCHANGE_RATE);

        responseDto = ExchangeRateResponseDto.builder()
                .disclaimer(DISCLAIMER)
                .license(LICENSE)
                .timestamp(TIMESTAMP)
                .base(BASE)
                .rates(rates)
                .build();
    }

    @Nested
    class PositiveTests {

        @Test
        @DisplayName("Успешная валидация ExchangeRateResponseDto")
        public void whenValidationExchangeRateResponseShouldSuccess() {
            assertDoesNotThrow(() -> exchangeRateValidator.validationExchangeRateResponse(responseDto));
        }

        @Test
        @DisplayName("Успех при наличии суммы обменного курса")
        public void whenValidationExchangeRateAmountShouldSuccess() {
            assertDoesNotThrow(() -> exchangeRateValidator
                    .validationExchangeRateAmount(BigDecimal.valueOf(EXCHANGE_RATE), CURRENCY));
        }
    }

    @Nested
    class NegativeTests {

        @Test
        @DisplayName("Ошибка при валидации ExchangeRateResponseDto")
        public void whenValidationExchangeRateResponseIsNullThrowException() {
            responseDto = null;
            assertThrows(RuntimeException.class,
                    () -> exchangeRateValidator.validationExchangeRateResponse(responseDto));
        }

        @Test
        @DisplayName("Ошибка при отсутствии суммы обменного курса")
        public void whenValidationExchangeRateAmountIsNullThrowException() {
            assertThrows(IllegalArgumentException.class,
                    () -> exchangeRateValidator.validationExchangeRateAmount(null, CURRENCY));
        }
    }
}