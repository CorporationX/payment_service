package faang.school.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @Test
    void testSendPayment() throws Exception {
        PaymentRequest paymentRequest = new PaymentRequest(1234, BigDecimal.valueOf(100.50), Currency.EUR);

        when(paymentService.calculateAmount(paymentRequest.amount(), paymentRequest.currency())).thenReturn(BigDecimal.valueOf(92.9212296750));

        mockMvc.perform(post("/api/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(paymentRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(PaymentStatus.SUCCESS.name()))
                .andExpect(jsonPath("$.paymentNumber").value(paymentRequest.paymentNumber()))
                .andExpect(jsonPath("$.currency").value(paymentRequest.currency().name()))
                .andExpect(jsonPath("$.amount").value(92.9212296750))
        ;
    }
}