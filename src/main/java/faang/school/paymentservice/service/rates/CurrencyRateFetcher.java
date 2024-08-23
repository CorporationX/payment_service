package faang.school.paymentservice.service.rates;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyRateFetcher {
    private final ExchangeRatesCash exchangeRatesCash;
    private final CurrencyService currencyService;

    @PostConstruct
    @Scheduled(cron = "${currency.cron}")
    public void getCurrencyRate() {
        exchangeRatesCash.getRates().clear();
        exchangeRatesCash.setRates(currencyService.getActualRates());
        log.info("Update actual currency rate and save in cash.");
    }

}
