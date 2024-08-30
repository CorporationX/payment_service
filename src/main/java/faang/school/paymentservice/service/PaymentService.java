package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.Currency;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final OpenExchangeRatesService openExchangeRatesService;

    @Value("${payment.exchangeFee}")
    private double exchangePee;

    @Value("${payment.baseCurrency}")
    private Currency baseCurrency;

    public BigDecimal calculateAmount(BigDecimal amount, Currency paymentCurrency) {
        BigDecimal exchange = openExchangeRatesService.exchange(baseCurrency, paymentCurrency);

        return amount
                .multiply(exchange)
                .multiply(BigDecimal.valueOf(exchangePee));
    }
}
