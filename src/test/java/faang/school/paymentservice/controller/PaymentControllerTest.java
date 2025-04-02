package faang.school.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(PaymentController.class)
@ContextConfiguration(classes = {PaymentController.class, GlobalExceptionHandler.class})
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private PaymentRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new PaymentRequest(
                123L,
                new BigDecimal("1000.00"),
                Currency.USD
        );
    }

    @Test
    void shouldReturn200WhenPaymentSuccessful() throws Exception {
        PaymentResponse mockResponse = new PaymentResponse(
                PaymentStatus.SUCCESS,
                1234,
                123L,
                new BigDecimal("1010.00"),
                Currency.USD,
                "Thank you!"
        );

        when(paymentService.sendPayment(any())).thenReturn(mockResponse);

        mockMvc.perform(post("/api/payment")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.amount").value(1010.00));

        verify(paymentService, times(1)).sendPayment(any());
    }

    @Test
    void shouldReturn400WhenIllegalArgument() throws Exception {
        when(paymentService.sendPayment(any())).thenThrow(new IllegalArgumentException("Invalid currency"));

        mockMvc.perform(post("/api/payment")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isBadRequest());

        verify(paymentService, times(1)).sendPayment(any());
    }
}
