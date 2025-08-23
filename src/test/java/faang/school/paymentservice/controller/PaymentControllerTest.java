package faang.school.paymentservice.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThan;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Тесты контроллера премиум-подписок")
@WebMvcTest(PaymentController.class)
public class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Успешный ответ при корректном платеже")
    void sendPayment_shouldReturnSuccessResponse() throws Exception {
        String jsonRequest = """
                {
                    "paymentNumber": 12345,
                    "amount": 100.00,
                    "currency": "USD"
                }
                """;

        mockMvc.perform(post("/api/payment")
                        .header("x-user-id", 42)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.paymentNumber").value(12345))
                .andExpect(jsonPath("$.amount").value(100.00))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.message", containsString("Thank you for your purchase")))
                .andExpect(jsonPath("$.verificationCode", greaterThanOrEqualTo(1000)))
                .andExpect(jsonPath("$.verificationCode", lessThan(10000)));
    }

    @Test
    @DisplayName("Ошибка 400 при некорректном теле запроса")
    void sendPayment_shouldReturnBadRequest_whenInvalidRequest() throws Exception {
        String invalidJsonRequest = "{}";

        mockMvc.perform(post("/api/payment")
                        .header("x-user-id", 42)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJsonRequest))
                .andExpect(status().isBadRequest());
    }
}
