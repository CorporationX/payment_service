package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.Currency;
import org.springframework.stereotype.Service;

@Service
public class PaymentMessageService {

    public String generateMessageAfterPayment(String formattedSum,
                                              Currency paymentRequestCurrency,
                                              String formattedSumInBaseCurrency,
                                              Currency baseCurrency) {
        if (paymentRequestCurrency == baseCurrency) {
            return String.format("Dear friend! Thank you for your purchase! Your payment on %s %s was accepted.",
                    formattedSum,
                    paymentRequestCurrency.name());
        }

        return String.format("Dear friend! Thank you for your purchase! Your payment on %s %s (%s %s) was accepted.",
                formattedSum,
                paymentRequestCurrency.name(),
                formattedSumInBaseCurrency,
                baseCurrency.name());
    }
}
