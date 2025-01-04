package faang.school.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.context.UserContext;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.payment.AuthorizationMessage;
import faang.school.paymentservice.dto.payment.AuthorizationResponse;
import faang.school.paymentservice.exeption.GetAuthorizationBadRequest;
import faang.school.paymentservice.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    @MockBean
    private UserContext userContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAuthorizationValid() throws Exception {
        when(userContext.getUserId()).thenReturn(1L);

        AuthorizationMessage authorizationMessage = init();

        AuthorizationResponse mockResponse = AuthorizationResponse.builder()
                .requestId(1L)
                .message("success")
                .verificationCode("123")
                .build();

        when(paymentService.authorizePayment(Mockito.any(AuthorizationMessage.class)))
                .thenReturn(mockResponse);

        mockMvc.perform(post("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorizationMessage)))
                .andExpect(status().isOk());
    }


    @Test
    void getAuthorizationInvalid() throws Exception {
        when(userContext.getUserId()).thenReturn(1L);

        AuthorizationMessage authorizationMessage = AuthorizationMessage.builder()
                .senderAccountId(1L)
                .recipientAccountId(1L)
                .senderNumber("1")
                .recipientAccountNumber("2")
                .currency(Currency.USD)
                .amount(BigDecimal.valueOf(1000))
                .build();

        when(paymentService.authorizePayment(Mockito.any(AuthorizationMessage.class)))
                .thenThrow(new GetAuthorizationBadRequest("Sender and recipient cannot be the same"));

        mockMvc.perform(post("/api")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authorizationMessage)))
                .andExpect(status().isBadRequest());
    }

    private AuthorizationMessage init() {
        return AuthorizationMessage.builder()
                .senderAccountId(1L)
                .recipientAccountId(2L)
                .senderNumber("1")
                .recipientAccountNumber("2")
                .currency(Currency.USD)
                .amount(BigDecimal.valueOf(1000))
                .build();
    }
}