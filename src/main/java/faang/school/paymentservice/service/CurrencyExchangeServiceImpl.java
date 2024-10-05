package faang.school.paymentservice.service;

import faang.school.paymentservice.client.OpenExchangesRatesClient;
import faang.school.paymentservice.config.currency.CurrencyExchangeParams;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.exchange.CurrencyExchangeResponse;
import faang.school.paymentservice.exception.ClientResponseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {
    private final CurrencyExchangeParams exchangeParams;
    private final OpenExchangesRatesClient ratesClient;

    @Override
    public BigDecimal convertWithCommission(PaymentRequest dto, Currency toCurrency) {
        CurrencyExchangeResponse response = ratesClient.getLatestRates(exchangeParams.getAppId());
        if (response == null) {
            throw new ClientResponseException("Failed to get latest rates");
        }
        if (!response.getRates().containsKey(toCurrency.name())) {
            throw new IllegalArgumentException("Currency %s not supported"
                    .formatted(toCurrency.name()));
        }
        double fromRate = response.getRates().get(dto.currency().name());
        double toRate = response.getRates().get(toCurrency.name());
        double conversionRate = toRate / fromRate;
        BigDecimal convertAmount = dto.amount().multiply(BigDecimal.valueOf(conversionRate));
        BigDecimal commissionAmount = BigDecimal.valueOf(1 + exchangeParams.getCommission());
        return convertAmount.multiply(commissionAmount);
    }
}