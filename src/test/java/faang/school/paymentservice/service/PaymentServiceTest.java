package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(properties = {
        "payment.exchangeFee=1.01",
        "payment.baseCurrency=USD"
})
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @MockBean
    private OpenExchangeRatesService openExchangeRatesService;


    @Test
    void testCalculateAmount() {
        BigDecimal amount = BigDecimal.valueOf(4);
        BigDecimal expected = amount
                .multiply(BigDecimal.valueOf(0.92))
                .multiply(BigDecimal.valueOf(1.01));

        when(openExchangeRatesService.exchange(Currency.USD, Currency.EUR)).thenReturn(BigDecimal.valueOf(0.92));

        BigDecimal result = paymentService.calculateAmount(amount, Currency.EUR);

        assertEquals(expected, result);
    }
}