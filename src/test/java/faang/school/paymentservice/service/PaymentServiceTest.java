package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class PaymentServiceTest {
    @MockBean
    private ExchangeRatesService exchangeRatesService;
    @Autowired
    private PaymentService paymentService;

    @Test
    void testSendPayment_Success() {
        long paymentNumber = 1111L;
        BigDecimal amount = new BigDecimal("100.00");
        Currency paymentCurrency = Currency.USD;
        Currency targetCurrency = Currency.EUR;
        BigDecimal rate = new BigDecimal("0.8");

        PaymentRequest request = new PaymentRequest(paymentNumber, amount, paymentCurrency, targetCurrency);

        when(exchangeRatesService.getExchangeRate(paymentCurrency, targetCurrency))
                .thenReturn(rate);

        PaymentResponse response = paymentService.sendPayment(request);

        BigDecimal expectedAmount = new BigDecimal("79.20");
        assertEquals(expectedAmount, response.amount());
    }
}