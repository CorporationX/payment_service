package faang.school.paymentservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import faang.school.paymentservice.config.ExchangeCurrencyConfig;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.service.PaymentService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@WebMvcTest(PaymentController.class)
@Import(PaymentControllerTest.TestConfig.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ExchangeCurrencyConfig currencyConfig;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public ExchangeCurrencyConfig currencyConfig() {
            return new ExchangeCurrencyConfig(
                    "https://openexchangerates.org",
                    "test-app-id",
                    Currency.USD,
                    1.0
            );
        }
    }

    @Test
    void testConfigValues() {
        assertEquals(1.0, currencyConfig.commission());  // Check injected value
    }


    @Test
    void testConvertCurrency() throws Exception {

        BigDecimal amount = new BigDecimal("100.0");
        Currency currencyFrom = Currency.USD;
        Currency currencyTo = Currency.CAD;
        BigDecimal convertedAmount = new BigDecimal("143.22");
        double commission = 1.0;

        when(paymentService.convert(amount, currencyFrom, currencyTo)).thenReturn(convertedAmount);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/convert/100.0/USD/CAD")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.amount").value(amount))
                .andExpect(jsonPath("$.currencyFrom").value(currencyFrom.name()))
                .andExpect(jsonPath("$.convertedAmount").value(convertedAmount))
                .andExpect(jsonPath("$.currencyTo").value(currencyTo.name()))
                .andExpect(jsonPath("$.message").exists());
    }
}
