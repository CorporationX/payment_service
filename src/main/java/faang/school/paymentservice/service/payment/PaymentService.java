package faang.school.paymentservice.service.payment;

import java.math.BigDecimal;

public interface PaymentService {
    BigDecimal convertCurrency(BigDecimal amount, String fromCurrency, String toCurrency);
}
