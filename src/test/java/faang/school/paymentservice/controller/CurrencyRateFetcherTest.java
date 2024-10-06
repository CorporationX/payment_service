package faang.school.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.CurrencyRateDto;
import faang.school.paymentservice.service.currency.CurrencyService;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
public class CurrencyRateFetcherTest {
    @InjectMocks
    private CurrencyRateFetcher controller;
    @Mock
    private CurrencyService service;
    private Map<Currency, Double> rates;
    private final String baseCurrency = "EUR";
    private final LocalDateTime now = LocalDateTime.now();
    private Map<Currency, Double> expectedRates;
    private MockMvc mockMvc;
    private CurrencyRateDto dto;
    private CurrencyRateDto expectedDto;
    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        rates = new HashMap<>();
        rates.put(Currency.RUB, 100.0);
        rates.put(Currency.USD, 1.1);
        expectedRates = new HashMap<>();
        expectedRates.put(Currency.RUB, 100.0);
        expectedRates.put(Currency.USD, 1.1);
        dto = new CurrencyRateDto(now, baseCurrency, rates);
        expectedDto = new CurrencyRateDto(now, baseCurrency, expectedRates);
    }

    @Test
    public void testUpdateActualCurrencyRate() {
        // Act & Assert
        controller.updateActualCurrencyRate();
        verify(service, times(1)).updateActualCurrencyRate();
    }

    @Test
    public void testCheckHealth() throws Exception {
        // Arrange
        when(service.getInfo()).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/api/currency-rate/health"))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(expectedDto)));
    }
}
