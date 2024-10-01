package faang.school.paymentservice.service;

import faang.school.paymentservice.client.OpenExchangesRatesClient;
import faang.school.paymentservice.config.currency.CurrencyExchangeConfig;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.exchange.CurrencyExchangeResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

@Service
@RequiredArgsConstructor
@Getter
public class CurrencyExchangeServiceImpl implements CurrencyExchangeService {
    private boolean isAddCommission = false;
    private final CurrencyExchangeConfig exchangeConfig;
    private final OpenExchangesRatesClient ratesClient;
    private final double commission = exchangeConfig.getCommission();
    private final String appId = exchangeConfig.getAppId();

    @Override
    public BigDecimal convertCurrency(Currency currency, Currency toCurrency,
                                      BigDecimal amount, String appId) {
        if (toCurrency == currency || toCurrency == null) {
            return amount;
        }
        CurrencyExchangeResponse response = ratesClient.getLatestRates(appId);
        if (!response.getRates().containsKey(toCurrency.name())) {
            throw new IllegalArgumentException("Currency %s not supported".formatted(toCurrency.name()));
        }
        if (currency.name().equals("USD")) {
            return amount.multiply(BigDecimal.valueOf
                    (response.getRates().get(toCurrency.name())));
        }
        if (toCurrency.name().equals("USD")) {
            return amount.divide(BigDecimal.valueOf(response.getRates().get(currency.name())), 4, RoundingMode.HALF_UP);
        }
        double fromRate = response.getRates().get(currency.name());
        BigDecimal usdAmount = amount.divide(BigDecimal.valueOf(fromRate), 4, RoundingMode.HALF_UP);
        double toRate = response.getRates().get(toCurrency.name());
        BigDecimal convertedAmount = usdAmount.multiply(BigDecimal.valueOf(toRate));
        return convertedAmount;
    }

    public BigDecimal addCommission(BigDecimal amount, double commission, Currency currency, Currency toCurrency) {
        if (currency.name().equals(toCurrency.name())) {
            return amount;
        }
        BigDecimal commissionAmount = BigDecimal.valueOf(commission);
        isAddCommission = true;
        return amount.multiply(commissionAmount);
    }

    public String getMessage(PaymentRequest dto, Currency toCurrency) {
        BigDecimal convertedAmount = convertCurrency(dto.currency(), toCurrency, dto.amount(), appId);
        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        String formattedSum = decimalFormat.format(addCommission(dto.amount(), getCommission(), dto.currency(), toCurrency));
        if (isAddCommission) {
            isAddCommission = false;
            return String.format("Dear friend! Thank you for your purchase! " +
                            "Converted amount is %.2f %s. You have to pay commission %s %s.",
                    convertedAmount, toCurrency, formattedSum, dto.currency().name());
        }
        return String.format("Dear friend! Thank you for your purchase!");
    }
}
