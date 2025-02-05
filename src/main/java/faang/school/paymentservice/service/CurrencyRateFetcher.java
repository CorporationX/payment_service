package faang.school.paymentservice.service;

import faang.school.paymentservice.mapper.CurrencyRateMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CurrencyRateFetcher {
    private final ExchangeRateService exchangeRateService;
    private final CurrencyRateService currencyRateService;
    private final CurrencyRateMapper mapper;

    @Scheduled(fixedRateString = "${currency.rate.updateExchangeRatesMillis}")
    public void fetchAndSaveCurrencyRates() {
        exchangeRateService.getCurrencyRateFromApi()
                .subscribe(currencyRate -> currencyRateService.save(mapper.toEntity(currencyRate)));
    }
}
