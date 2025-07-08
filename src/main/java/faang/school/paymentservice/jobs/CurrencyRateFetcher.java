package faang.school.paymentservice.jobs;

import faang.school.paymentservice.dto.ShortCurrency;
import faang.school.paymentservice.service.currency.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.EnumSet;

@Slf4j
@RequiredArgsConstructor
@Component
public class CurrencyRateFetcher {

    private final CurrencyService currencyService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void fetchCurrencyRate() {
        EnumSet<ShortCurrency> currencies = EnumSet.allOf(ShortCurrency.class);
        log.info("fetchCurrencyRate currencies = {}", currencies);
        currencyService.fillCrossRatesCash(ShortCurrency.USD, currencies);
        log.info("fetchCurrencyRate finished");
    }
}
