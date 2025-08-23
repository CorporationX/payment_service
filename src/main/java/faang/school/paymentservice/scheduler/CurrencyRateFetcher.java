package faang.school.paymentservice.scheduler;

import faang.school.paymentservice.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyRateFetcher {

    private final CurrencyService service;
    private HashMap<String, Double> actualRates;

    @Scheduled(cron = "${currency.rates.cron}")
    @Retr
    public void ratesCron() {
        service.getActualRates()
                .subscribe(rates -> actualRates.putAll(rates));
    }
}
