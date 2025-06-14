package faang.school.paymentservice.service;

import faang.school.paymentservice.config.ConverterClient;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.CurrencyExchangeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    @Value("${url.id}")
    private String appId;

    @Value("${exchange.currency}")
    private String currency;

    @Value("${exchange.commission}")
    private Long commission;


    private final ConverterClient converterClient;

    public BigDecimal convertToRUB(PaymentRequest dto) {
        CurrencyExchangeResponse response =
                converterClient.getCurrentCurrencyExchangeRate(appId, dto.currency().name(), "RUB");

        BigDecimal rub = BigDecimal.valueOf(response.getRates().get(currency));

        double percentDouble = 1. + (double) commission / 100;
        BigDecimal percent = new BigDecimal(percentDouble);

        return dto.amount().multiply(rub).multiply(percent);
    }
}
