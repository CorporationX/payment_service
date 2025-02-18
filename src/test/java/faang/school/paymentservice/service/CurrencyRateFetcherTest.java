package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.CurrencyRate;
import faang.school.paymentservice.entity.CurrencyRateDto;
import faang.school.paymentservice.mapper.CurrencyRateMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyRateFetcherTest {

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private CurrencyRateService currencyRateService;

    @Mock
    private CurrencyRateMapper mapper;

    @InjectMocks
    private CurrencyRateFetcher currencyRateFetcher;

    private CurrencyRateDto dto;
    private CurrencyRate currency;

    @BeforeEach
    void setUp() {
        Map<Currency, Double> rates = Map.of(Currency.AED, 1.867274, Currency.SEK, 946.309397);
        dto = CurrencyRateDto.builder()
                .success(true)
                .timestamp(1738848543L)
                .base(Currency.EUR)
                .date("2025-01-01")
                .rates(rates)
                .build();
        currency = CurrencyRate.builder()
                .timestamp(LocalDateTime.now())
                .rates(rates)
                .build();
    }

    @Test
    void fetchAndSaveCurrencyRates_ShouldFetchAndSaveRates() {
        when(exchangeRateService.getCurrencyRateFromApi()).thenReturn(Mono.just(dto));
        when(mapper.toEntity(dto)).thenReturn(currency);

        currencyRateFetcher.fetchAndSaveCurrencyRates();

        verify(exchangeRateService, times(1)).getCurrencyRateFromApi();
        verify(currencyRateService, times(1)).save(currency);
    }

    @Test
    void fetchAndSaveCurrencyRates_ShouldHandleErrorGracefully() {
        when(exchangeRateService.getCurrencyRateFromApi()).thenReturn(Mono.error(new RuntimeException("API not available")));

        currencyRateFetcher.fetchAndSaveCurrencyRates();

        verify(exchangeRateService, times(1)).getCurrencyRateFromApi();
        verify(currencyRateService, never()).save(any());
    }

}