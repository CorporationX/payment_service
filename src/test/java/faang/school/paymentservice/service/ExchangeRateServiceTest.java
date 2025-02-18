package faang.school.paymentservice.service;

import faang.school.paymentservice.config.CurrencyRateConfig;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.entity.CurrencyRateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeRateServiceTest {
    @Mock
    private CurrencyRateConfig config;

    @Mock
    private WebClient webClient;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpec;
    @Mock
    private WebClient.ResponseSpec responseSpec;

    @InjectMocks
    private ExchangeRateService exchangeRateService;

    private CurrencyRateDto dto;

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
    }

    @Test
    void getCurrencyRateFromApi_ShouldReturnCurrencyRateDto() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CurrencyRateDto.class)).thenReturn(Mono.just(dto));

        Mono<CurrencyRateDto> result = exchangeRateService.getCurrencyRateFromApi();

        result.subscribe(response -> {
            assertNotNull(response);
            assertTrue(response.success());
        });

        verify(webClient, times(1)).get();
        verify(requestHeadersUriSpec, times(1)).retrieve();
        verify(responseSpec, times(1)).bodyToMono(CurrencyRateDto.class);
    }

    @Test
    void getCurrencyRateFromApi_ShouldHandleError() {
        when(webClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.bodyToMono(CurrencyRateDto.class)).thenReturn(Mono.error(new RuntimeException("API not available")));

        Mono<CurrencyRateDto> result = exchangeRateService.getCurrencyRateFromApi();

        result.onErrorResume(throwable -> {
            assertInstanceOf(RuntimeException.class, throwable);
            assertEquals("API not available", throwable.getMessage());
            return Mono.empty();
        }).subscribe();

        verify(webClient, times(1)).get();
        verify(requestHeadersUriSpec, times(1)).retrieve();
        verify(responseSpec, times(1)).bodyToMono(CurrencyRateDto.class);
    }
}