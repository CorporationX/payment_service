package faang.school.paymentservice.service.currency;

import faang.school.paymentservice.client.ExchangeServiceClient;
import faang.school.paymentservice.dto.ExchangeResponse;
import faang.school.paymentservice.service.RedisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import faang.school.paymentservice.properties.ExchangeServiceProperties;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class CurrencyRateFetcherImpl implements CurrencyRateFetcher {

    private final ExchangeServiceProperties exchangeServiceProperties;
    private final ExchangeServiceClient exchangeServiceClient;
    private final RedisService redisService;
    @Value("${services.exchange-service.redis-key}")
    private String redisKey;

    @Override
    @Scheduled(cron = "${services.exchange-service.cron-expression}")
    public void exchangeCurrency() {
        log.info("Start, fetch exchange rates from an external API");
        ExchangeResponse response = exchangeServiceClient.exchange(exchangeServiceProperties.getToken());
        Map<String, Double> rates = response.rates();
        redisService.save(redisKey, rates);
        log.info("End, fetch exchange rates from an external API");
    }
}