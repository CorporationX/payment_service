package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.service.currency.rates.ExchangeRatesService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final RedisTemplate<String, BigDecimal> redisTemplate;
    private final ExchangeRatesService exchangeRatesService;

    public void getExchangeRates() {
        exchangeRatesService.getExchangeRates();
    }
}
