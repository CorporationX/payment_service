package faang.school.paymentservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.config.context.UserContext;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.service.payment.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {
    private MockMvc mockMvc;
    @MockBean
    private PaymentService paymentService;
    @MockBean
    private UserContext userContext;

    @BeforeEach
    void setUp() {
        this.mockMvc = standaloneSetup(new PaymentController(paymentService)).build();
    }

    @Test
    public void testSendPayment() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Long paymentNumber = 1L;
        BigDecimal amount = BigDecimal.valueOf(100);
        String message = "Success";
        PaymentRequest dto =  new PaymentRequest(paymentNumber, amount, Currency.EUR);
        PaymentResponse response = new PaymentResponse(
                PaymentStatus.SUCCESS, 2, paymentNumber, amount, Currency.USD, message);

        String jsonDto = objectMapper.writeValueAsString(dto);

        when(paymentService.processPayment(dto)).thenReturn(response);

        mockMvc.perform(post("/api/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonDto)
                        .header("x-user-id", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

}
