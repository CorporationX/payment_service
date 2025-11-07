package faang.school.paymentservice.service.currencyrate;

import faang.school.paymentservice.dto.Currency;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrencyService {

    private final CurrencyApiClient currencyApiClient;
    @Getter
    private final Map<LocalDateTime, CurrencyRateDto> currencyRates = new HashMap<>();
    @Getter
    private CurrencyRateDto latestRate;

    @Scheduled(cron = "${currency.scheduled.daily-task}")
    public void currencyFetchAndStorage() {
        try {
            CurrencyRateDto rate = currencyApiClient.fetchLatestRates();
            if (rate != null && rate.rates() != null) {
                currencyRates.put(rate.date(), rate);
                latestRate = rate;
                log.info("Stored currency rates for date: {}. Available currencies: {}",
                        rate.date(), rate.rates().keySet());
            }
        } catch (Exception e) {
            log.error("Failed to fetch and store currency rates after retries", e);
        }
    }

    public Double getCurrencyRateByCurrency(Currency currency) {
        return latestRate.rates().get(currency);
    }
}
