package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.service.exchange.ExchangeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final ExchangeService exchangeService;
    @Value("${app.currency.base}")
    private String baseCurrency;
    private static final String RESPONSE_WITHOUT_EXCHANGE = "Dear friend! Thank you for your purchase! " +
            "Your payment on %s %s was accepted.";
    private static final String RESPONSE_WITH_EXCHANGE = "Dear friend! Thank you for your purchase! " +
            "Your payment on %s %s converted to %s %s and was accepted.";
    private DecimalFormat decimalFormat = new DecimalFormat("0.00");

    public PaymentResponse processPayment(PaymentRequest dto) {
        BigDecimal amount = dto.amount();
        String formattedSum = decimalFormat.format(amount);
        int verificationCode = new Random().nextInt(1000, 10000);
        String message = String.format(RESPONSE_WITHOUT_EXCHANGE, formattedSum, dto.currency().name());

        if (!exchangeService.isCurrencyBase(dto)) {
            amount = exchangeService.getAmountInBaseCurrency(dto);
            String formattedNewSum = decimalFormat.format(amount);
            message = String.format(RESPONSE_WITH_EXCHANGE, formattedSum,
                    dto.currency().name(), formattedNewSum, baseCurrency);
        }
        return new PaymentResponse(
                PaymentStatus.SUCCESS,
                verificationCode,
                dto.paymentNumber(),
                amount,
                Currency.valueOf(baseCurrency),
                message);
    }
}
