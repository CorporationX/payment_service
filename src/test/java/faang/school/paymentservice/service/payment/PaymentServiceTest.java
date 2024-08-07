package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.service.exchange.ExchangeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @Mock
    private ExchangeService exchangeService;
    @InjectMocks
    private PaymentService paymentService;
    private Long paymentNumber = 1L;
    private BigDecimal amount = BigDecimal.valueOf(100);
    private PaymentRequest dto = new PaymentRequest(paymentNumber, amount, Currency.USD);
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");
    String formattedSum = decimalFormat.format(amount);
    private String baseCurrency = "USD";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentService, "baseCurrency", baseCurrency);
    }

    @Test
    public void testProcessPaymentWithBaseCurrency() {
        when(exchangeService.isCurrencyBase(dto)).thenReturn(true);

        String messageExpected = String.format("Dear friend! Thank you for your purchase! " +
                "Your payment on %s %s was accepted.", formattedSum, baseCurrency);

        PaymentResponse result = paymentService.processPayment(dto);
        assertEquals(amount, result.amount());
        assertEquals(paymentNumber, result.paymentNumber());
        assertEquals(baseCurrency, result.currency().name());
        assertEquals(messageExpected, result.message());
    }

    @Test
    public void testProcessPaymentWithExchangeCurrency() {
        dto = new PaymentRequest(paymentNumber, amount, Currency.EUR);
        BigDecimal amountNew = BigDecimal.valueOf(90);
        String formattedNewSum = decimalFormat.format(amountNew);

        when(exchangeService.getAmountInBaseCurrency(dto)).thenReturn(amountNew);

        String messageExpected = String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %s %s converted to %s %s and was accepted.",
                formattedSum, Currency.EUR, formattedNewSum, baseCurrency);
        PaymentResponse result = paymentService.processPayment(dto);

        assertEquals(amountNew, result.amount());
        assertEquals(paymentNumber, result.paymentNumber());
        assertEquals(baseCurrency, result.currency().name());
        assertEquals(messageExpected, result.message());
    }
}
