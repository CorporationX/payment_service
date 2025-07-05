package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.client.CurrencyClient;
import faang.school.paymentservice.dto.ExchangeRateResponseDto;
import faang.school.paymentservice.dto.ShortCurrency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.AbstractMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static faang.school.paymentservice.utils.MessageConstants.NO_INFO_FOR_KEY;

@Slf4j
@RequiredArgsConstructor
@Service
public class CurrencyService {

    private final CurrencyClient currencyClient;
    private final CurrencyCacheService currencyCacheService;

    @Value("${currency.round-digits}")
    private int roundDigits;

    public void fillCrossRatesCash(ShortCurrency base, EnumSet<ShortCurrency> currencies) {
        String currencyList = currencies.stream()
                .filter(currency -> !Objects.equals(currency, base))
                .map(Enum::name)
                .collect(Collectors.joining(","));

        ExchangeRateResponseDto response = currencyClient.getLiveExchangeRate(base.name(), currencyList);
        log.info("response = {}", response);

        Map<String, BigDecimal> quotes = response.getQuotes();
        quotes.forEach(currencyCacheService::saveRate);
        Map<String, BigDecimal> ratesFromBaseMap = getRatesFromBaseMap(base, currencies, quotes);

        currencies.stream()
                .filter(currency -> !Objects.equals(currency, base))
                .forEach(currency -> ratesFromBaseMap.entrySet().stream()
                        .filter(entry -> !Objects.equals(entry.getKey(), currency.name()))
                        .forEach(entry -> {
                            String key = currency.name() + entry.getKey();
                            BigDecimal from = ratesFromBaseMap.get(currency.name());
                            BigDecimal to = ratesFromBaseMap.get(entry.getKey());
                            BigDecimal rate = to.divide(from, roundDigits, RoundingMode.HALF_EVEN);
                            currencyCacheService.saveRate(key, rate);
                        }));
    }

    private Map<String, BigDecimal> getRatesFromBaseMap(ShortCurrency base, EnumSet<ShortCurrency> currencies, Map<String, BigDecimal> quotes) {

        Map<String, BigDecimal> ratesFromBase = currencies.stream()
                .filter(currency -> !Objects.equals(currency, base))
                .map(currency -> {
                    String key = base.name() + currency.name();
                    BigDecimal rate = quotes.get(key);
                    if (rate == null) {
                        log.error(String.format(NO_INFO_FOR_KEY, key));
                        throw new RuntimeException(String.format(NO_INFO_FOR_KEY, key));
                    }
                    return new AbstractMap.SimpleEntry<>(currency.name(), rate);
                })
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
        ratesFromBase.put(base.name(), new BigDecimal("1.0"));
        log.info("ratesFromBase = {}", ratesFromBase);
        return ratesFromBase;
    }
}
