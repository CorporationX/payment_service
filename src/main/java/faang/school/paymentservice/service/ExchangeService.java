package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeServiceClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.CurrencyRate;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.exception.ServiceInteractionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final ExchangeServiceClient exchangeServiceClient;

    private final Random random = new Random();

    @Value("${services.exchange.app-id}")
    private String appId;

    @Value("${services.exchange.fee}")
    private BigDecimal fee;

    public PaymentResponse sendPayment(PaymentRequest paymentRequest) {
        BigDecimal totalAmount = getTotalAmount(paymentRequest);
        int verificationCode = random.nextInt(1000, 10000);
        String message = getSuccessMessage(totalAmount, paymentRequest);
        PaymentResponse paymentResponse = new PaymentResponse(
                PaymentStatus.SUCCESS,
                verificationCode,
                paymentRequest.paymentNumber(),
                totalAmount,
                paymentRequest.currency(),
                message
        );
        log.info("Success purchase on {} {} with payment number {}",
                totalAmount,
                paymentRequest.currency().name(),
                paymentRequest.paymentNumber()
        );
        return paymentResponse;
    }

    private BigDecimal getTotalAmount(PaymentRequest paymentRequest) {
        Currency currency = paymentRequest.currency();
        CurrencyRate currencyRate = exchangeServiceClient.getCurrencyRate(appId, currency);
        BigDecimal rate = getRate(currencyRate, currency);
        if (Currency.USD.equals(currency)) {
            return paymentRequest.amount();
        }
        return paymentRequest.amount()
                .multiply(rate)
                .multiply(fee);
    }

    private String getSuccessMessage(BigDecimal totalAmount, PaymentRequest paymentRequest) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSum = decimalFormat.format(totalAmount);
        return String.format("Dear friend! Thank you for your purchase! Your payment on %s %s was accepted.",
                formattedSum, paymentRequest.currency().name());
    }

    private BigDecimal getRate(CurrencyRate currencyRate, Currency currency) {
        BigDecimal rate = currencyRate.getRates().get(currency.name());
        if (rate == null) {
            log.error("Currency rate {} not found", currency.name());
            throw new ServiceInteractionException("Currency rate not found for currency " + currency.name());
        }
        return rate;
    }
}
