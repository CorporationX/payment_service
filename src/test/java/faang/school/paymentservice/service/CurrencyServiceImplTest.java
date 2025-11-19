package faang.school.paymentservice.service;

import faang.school.paymentservice.service.currency.CurrencyRateCache;
import faang.school.paymentservice.service.currency.CurrencyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyRateServiceTest {

    @Mock
    private CurrencyRateCache currencyRateCache;

    @InjectMocks
    private CurrencyServiceImpl service;

    private Map<String, BigDecimal> fakeRates;

    @BeforeEach
    void setUp() {
        fakeRates = Map.of(
                "USD", new BigDecimal("95.500000"),
                "EUR", new BigDecimal("103.200000"),
                "CNY", new BigDecimal("13.450000"),
                "RUB", BigDecimal.ONE
        );
    }

    @Test
    void getCurrencyRate_returnsCorrectlyFormattedString() {
        when(currencyRateCache.getAllRates()).thenReturn(fakeRates);

        String result = service.getCurrencyRate();

        String expected = """
                CNY-13.450000
                EUR-103.200000
                RUB-1.000000
                USD-95.500000""";

        assertThat(result)
                .contains("USD-95.500000")
                .contains("EUR-103.200000")
                .contains("CNY-13.450000")
                .contains("RUB-1.000000");

        assertThat(result.lines().count()).isEqualTo(4);
    }

    @Test
    void getCurrencyRate_withSortedOutput_ifYouWantStableOrder() {
        when(currencyRateCache.getAllRates()).thenReturn(fakeRates);

        String result = service.getCurrencyRate();

        assertThat(result).isNotBlank();
        assertThat(result).contains("USD-95.500000", "RUB-1.000000");
    }

    @Test
    void clearRates_callsInvalidateOnCache() {
        service.clearRates();

        verify(currencyRateCache, times(1)).invalidateAll();
        verifyNoMoreInteractions(currencyRateCache);
    }

    @Test
    void getCurrencyRate_handlesEmptyMap() {
        when(currencyRateCache.getAllRates()).thenReturn(Map.of());

        String result = service.getCurrencyRate();

        assertThat(result).isEmpty();
    }
}