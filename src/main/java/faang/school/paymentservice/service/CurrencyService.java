package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeRates;
import faang.school.paymentservice.dto.ExchangeRatesDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CurrencyService {
    private final ExchangeRates exchangeRates;
    private final RedisTemplate<String, String> redisTemplate;


    public void currencyRateFetcher() {
        ExchangeRatesDto exchangeRatesDto = exchangeRates.fetchData().block();
        if (exchangeRatesDto.getSuccess() != null) {
            exchangeRatesDto.getRates().forEach((key, value) -> redisTemplate.opsForValue().set(key, value));

        } else {
            System.out.println("something happened");
        }
    }
}
