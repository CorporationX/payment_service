package faang.school.paymentservice.config;


import faang.school.paymentservice.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyRateFetcher {

    private final CurrencyService currencyService;

    @Scheduled(cron = "${currency.fetch.cron}")
    public void fetchRatesJob() {
        log.info("Запуск планировщика для обновления курсов валют");
        currencyService.fetchAndStoreRates();
    }
}
