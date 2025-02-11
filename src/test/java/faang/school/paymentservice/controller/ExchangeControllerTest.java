package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.service.ExchangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class ExchangeControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ExchangeService exchangeService;

    @InjectMocks
    private ExchangeController exchangeController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(exchangeController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Test converting is success")
    public void testConvert_Success() throws Exception {
        Mockito.when(exchangeService.convert(any(Double.class), any(Currency.class))).thenReturn(110.0);

        mockMvc.perform(get("/api/v1/exchange/converter/EUR").param("value", "100"))
                .andExpect(status().isOk())
                .andExpect(content().string("110.0"));
    }

    @Test
    @DisplayName("Test throw bad request if currency parameter is invalid")
    public void testConvert_InvalidCurrency() throws Exception {
        mockMvc.perform(get("/api/v1/exchange/converter/INVALID").param("value", "100"))
                .andExpect(status().isBadRequest());
    }
}