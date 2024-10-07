package faang.school.paymentservice.controller;

import faang.school.paymentservice.model.enums.Currency;
import faang.school.paymentservice.model.dto.PaymentRequest;
import faang.school.paymentservice.service.impl.CurrencyConverter;
import faang.school.paymentservice.validator.ValidatorPaymentController;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@RequiredArgsConstructor
class PaymentControllerTest {

    @Mock
    private ValidatorPaymentController validator;

    @Mock
    private CurrencyConverter currencyConverter;


    @InjectMocks
    private PaymentController paymentController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(paymentController).build();
    }

    @Test
    void sendPayment() throws Exception {
        Long paymentNumber = 23L;
        BigDecimal expectedAmount = new BigDecimal(1000);
        Currency currency = Currency.RUB;
        Currency currencyUsd = Currency.USD;
        PaymentRequest dto = new PaymentRequest(paymentNumber, expectedAmount, Currency.USD);

        doNothing().when(validator).checkCurrency(currencyUsd);
        when(currencyConverter.getLatestExchangeRates(dto, currency)).thenReturn(expectedAmount);

        mockMvc.perform(post("/api/v1/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"amount\": 1000, \"currency\": \"" + currencyUsd + "\", " +
                                "\"paymentNumber\": \"" + paymentNumber + "\" }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.paymentNumber").value(paymentNumber))
                .andExpect(jsonPath("$.amount").value(expectedAmount))
                .andExpect(jsonPath("$.currency").value("RUB"))
                .andExpect(jsonPath("$.message").value("Dear friend! Thank you for your purchase! " +
                        "Your payment on " + expectedAmount + ",00 RUB was accepted."));

        verify(validator, times(1)).checkCurrency(currencyUsd);
        verify(currencyConverter, times(1)).getLatestExchangeRates(any(PaymentRequest.class), eq(currency));
    }

}