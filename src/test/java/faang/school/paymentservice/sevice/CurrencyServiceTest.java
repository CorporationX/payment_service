package faang.school.paymentservice.sevice;

import faang.school.paymentservice.client.currency.CurrencyClient;
import faang.school.paymentservice.dto.CurrencyRateDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {
    @InjectMocks
    private CurrencyService service;
    @Mock
    private CurrencyClient currencyClient;

    @Test
    @DisplayName("Успешное получение курсов волют")
    void positive_shouldFetchCurrencyRates() {
        CurrencyRateDto expected = new CurrencyRateDto(
                true, 1519296206L, "EUR", LocalDate.now(), Map.of("EUR", 1.566015, "USD", 1.560132), null);
        when(currencyClient.getCurrencyRates()).thenReturn(Mono.just(expected));

        CurrencyRateDto actual = service.fetchCurrencyRates();

        assertEquals(expected, actual);
    }
}