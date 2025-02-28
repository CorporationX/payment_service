package faang.school.paymentservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

import faang.school.paymentservice.config.ExchangeCurrencyProperties;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Autowired
    private ExchangeCurrencyProperties currencyProperties;

    @BeforeEach
    public void init() {
        currencyProperties = new ExchangeCurrencyProperties(
                    "https://openexchangerates.org",
                    "test-app-id",
                    Currency.USD,
                new BigDecimal("1.01")
            );
    }

    @Test
    void testConfigValues() {
        assertEquals(new BigDecimal("1.01"), currencyProperties.commission());
    }

    @Test
    void testConvertCurrency() throws Exception {

        BigDecimal amount = new BigDecimal("100.01");
        Currency currencyFrom = Currency.USD;
        Currency currencyTo = Currency.CAD;
        BigDecimal convertedAmount = new BigDecimal("143.22");

        when(paymentService.convert(amount, currencyFrom, currencyTo)).thenReturn(convertedAmount);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/exchange")
                        .param("amount", amount.toString())
                        .param("currencyFrom", currencyFrom.name())
                        .param("currencyTo", currencyTo.name())
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
