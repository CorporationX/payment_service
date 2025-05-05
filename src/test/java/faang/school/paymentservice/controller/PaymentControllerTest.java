package faang.school.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    private PaymentRequest validPaymentRequest;
    private PaymentResponse successResponse;
    private final UUID testPaymentId = UUID.randomUUID();

    @BeforeEach
    public void setUp() {
        validPaymentRequest = new PaymentRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                new BigDecimal("100.00"),
                Currency.EUR,
                Instant.now()
        );

        successResponse = new PaymentResponse(
                PaymentStatus.PENDING,
                0,
                100,
                new BigDecimal("100.00"),
                Currency.EUR,
                ""
        );

    }

    @Nested
    class initiatePaymentTest {

        @Test
        public void givenValidData_whenInitiatePayment_thenSuccess() throws Exception {
            when(paymentService.initiatePayment(any(PaymentRequest.class)))
                    .thenReturn(successResponse);

            mockMvc.perform(post("/api")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(validPaymentRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("PENDING"));

            verify(paymentService).initiatePayment(any(PaymentRequest.class));
        }

        @Test
        public void givenNegativeAmount_whenInitiatePayment_thenFailure() throws Exception {
            PaymentRequest invalidRequest = new PaymentRequest(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    new BigDecimal("-100.00"),
                    Currency.EUR,
                    Instant.now()
            );

            mockMvc.perform(post("/api")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void givenMissingSenderId_whenInitiatePayment_thenFailure() throws Exception {
            PaymentRequest invalidRequest = new PaymentRequest(
                    null,
                    UUID.randomUUID(),
                    new BigDecimal("100.00"),
                    Currency.EUR,
                    Instant.now()
            );

            mockMvc.perform(post("/api")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }

        @Test
        public void givenMissingCurrency_whenInitiatePayment_thenFailure() throws Exception {
            PaymentRequest invalidRequest = new PaymentRequest(
                    UUID.randomUUID(),
                    UUID.randomUUID(),
                    new BigDecimal("100.00"),
                    null,
                    Instant.now()
            );

            mockMvc.perform(post("/api")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(invalidRequest)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    class ForcedPaymentTest {
        @Test
        public void givenValidPaymentId_whenForcedPayment_thenSuccess() throws Exception {
            PaymentResponse forcedResponse = new PaymentResponse(
                    PaymentStatus.CLEARED,
                    5,
                    3,
                    new BigDecimal("100.00"),
                    Currency.EUR,
                    "test"
            );

            when(paymentService.forcedPayment(testPaymentId))
                    .thenReturn(forcedResponse);

            mockMvc.perform(post("/api/{id}/forced-payment", testPaymentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("CLEARED"))
                    .andExpect(jsonPath("$.message").value("test"));

            verify(paymentService).forcedPayment(testPaymentId);
        }
    }

    @Nested
    class CancelPaymentTest {

        @Test
        public void givenValidPaymentId_whenCancelPayment_thenSuccess() throws Exception {
            PaymentResponse cancelResponse = new PaymentResponse(
                    PaymentStatus.CANCELED,
                    5,
                    3,
                    new BigDecimal("100.00"),
                    Currency.EUR,
                    "test"
            );

            when(paymentService.cancelPayment(testPaymentId))
                    .thenReturn(cancelResponse);

            mockMvc.perform(post("/api/{id}/cancel", testPaymentId)
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.status").value("CANCELED"))
                    .andExpect(jsonPath("$.message").value("test"));

            verify(paymentService).cancelPayment(testPaymentId);
        }
    }

}
