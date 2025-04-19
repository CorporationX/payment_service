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

    @Scheduled(cron = "${currency.rate.update.cron}")
    public void fetchCurrencyRates() {
        log.info("Запуск обновления курсов валют...");
        currencyService.updateCurrencyRates()
                .doOnSuccess(aVoid -> log.info("Курсы валют успешно обновлены."))
                .doOnError(throwable -> log.error("Ошибка при обновлении курсов валют: {}", throwable.getMessage()))
                .subscribe();
    }
}
