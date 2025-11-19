package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.config.currencyRate.CurrencyRateFetcherConfig;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.Map;

@Setter
@RequiredArgsConstructor
@Service
public class MapCurrencyServiceImpl implements MapCurrencyService{
    private final CurrencyRateFetcherConfig currencyRateFetcherConfig;
    private Map<String, Double> mapCurrentRate;

    @Bean
    public Map<String, Double> mapCurrentRate() {
        if (mapCurrentRate == null) {
            mapCurrentRate = currencyRateFetcherConfig.getCurrentRate();
        }
        return mapCurrentRate;
    }
}