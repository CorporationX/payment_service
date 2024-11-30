package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequestDto;
import faang.school.paymentservice.service.currency.rates.ExchangeRatesService;
import faang.school.paymentservice.validator.CurrencyValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CurrencyServiceTest {


    @Mock
    private RedisTemplate<String, Double> redisTemplate;

    @Mock
    private ValueOperations<String, Double> valueOperations;

    @Mock
    private ExchangeRatesService exchangeRatesService;

    @Mock
    private CurrencyValidator currencyValidator;

    @InjectMocks
    private CurrencyService currencyService;

    private BigDecimal commission;
    private Currency baseCurrency;

    @BeforeEach
    void setUp() {
        commission = new BigDecimal("0.01");
        baseCurrency = Currency.EUR;
        ReflectionTestUtils.setField(currencyService, "baseCurrency", baseCurrency);
        ReflectionTestUtils.setField(currencyService, "commission", commission);
    }

    @Test
    void testSuccessfulAttemptToGetExchangeRates() {
        Mono<String> expectedResult = Mono.just("USD: 1.23");
        when(exchangeRatesService.getExchangeRates()).thenReturn(Mono.just("USD: 1.23"));

        Mono<String> actualResult = currencyService.getExchangeRates();

        assertEquals(expectedResult.block(), actualResult.block());
    }

    @Test
    void testSuccessfulConvertCurrencyIfRequestCurrencyNotEqualToBaseCurrency() {
        PaymentRequestDto dto = PaymentRequestDto.builder()
                .amount(new BigDecimal("100.00"))
                .currency(Currency.AUD)
                .build();
        String formattedSum = prepareFormattedString(dto.getAmount());
        BigDecimal expectedAmount = prepareExpectedAmount(new BigDecimal("61.72"));

        when(redisTemplate.keys(dto.getCurrency().name())).thenReturn(new HashSet<>(Set.of("")));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(1.62);
        when(currencyService.getExchangeRateByCurrency(dto.getCurrency())).thenReturn((1.62));

        BigDecimal actualAmount = currencyService.convertCurrency(dto, formattedSum);

        assertEquals(expectedAmount, actualAmount);
        verify(currencyValidator, times(1)).validateMinimumTransferAmount(expectedAmount);
    }

    @Test
    void testSuccessfulConvertCurrencyIfRequestCurrencyEqualToBaseCurrency() {
        PaymentRequestDto dto = PaymentRequestDto.builder()
                .amount(new BigDecimal("100.00"))
                .currency(Currency.EUR)
                .build();
        String formattedSum = prepareFormattedString(dto.getAmount());
        BigDecimal expectedAmount = new BigDecimal("100.00");

        BigDecimal actualAmount = currencyService.convertCurrency(dto, formattedSum);

        assertEquals(expectedAmount, actualAmount);
        verify(currencyValidator, times(1)).validateMinimumTransferAmount(expectedAmount);
    }

    @Test
    void testGetExchangeRateByCurrencyIfRedisKeyIsNull() {
        Currency currency = Currency.BHD;

        when(redisTemplate.keys(currency.name())).thenReturn(null);

        Double actualRate = currencyService.getExchangeRateByCurrency(currency);

        assertEquals(0.0, actualRate);
    }

    @Test
    void testGetExchangeRateByCurrencyIfRateFromCurrencyIsNull() {
        Currency currency = Currency.BHD;

        when(redisTemplate.keys(currency.name())).thenReturn(new HashSet<>(Set.of("")));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(null);

        Double actualRate = currencyService.getExchangeRateByCurrency(currency);

        assertNull(actualRate);
    }

    @Test
    void testGetExchangeRateByCurrencyIfRateFromCurrencyAndRedisKeyAreExist() {
        Currency currency = Currency.BHD;

        when(redisTemplate.keys(currency.name())).thenReturn(new HashSet<>(Set.of("")));
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(anyString())).thenReturn(1.62);

        Double actualRate = currencyService.getExchangeRateByCurrency(currency);

        assertEquals(1.62, actualRate);
    }

    private BigDecimal prepareExpectedAmount(BigDecimal decimal) {
        BigDecimal amountCommission = decimal.multiply(commission);
        return decimal.subtract(amountCommission);
    }

    private String prepareFormattedString(BigDecimal amountFromDto) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("0.00", symbols);
        return decimalFormat.format(amountFromDto);
    }
}
