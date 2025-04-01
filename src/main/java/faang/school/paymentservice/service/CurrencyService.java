package faang.school.paymentservice.service;

import faang.school.paymentservice.model.CurrencyResponse;
import faang.school.paymentservice.model.LatestRatesEndpoint;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
@EnableConfigurationProperties({LatestRatesEndpoint.class})
public class CurrencyService {

    private final WebClient webClient;
    private final LatestRatesEndpoint endpoint;

    private CurrencyResponse currencyResponse;
    private Map<String, Double> currencyRates;

    @Retryable(value = {Exception.class}, maxAttempts = 5, backoff = @Backoff(delay = 2000))
    public void updateCurrencyRates() {
        webClient
                .get()
                .uri(endpoint.url() + "?access_key=" + endpoint.access_key() +
                        "&base=" + endpoint.base() + "&symbols=" + endpoint.symbols())
                .retrieve()
                .bodyToMono(CurrencyResponse.class)
                .subscribe(
                        response -> {
                            this.currencyResponse = response;
                            currencyRates = response.getRates();
                            System.out.println("Курсы валют успешно обновлены: " + currencyRates);
                            System.out.println(currencyResponse.getBase());
                        },
                        throwable -> {
                            System.err.println("Ошибка при обновлении курсов валют: " + throwable.getMessage());
                        }
                );
    }

    public CurrencyResponse getCurrencyResponse() {
        return currencyResponse;
    }

    public Map<String, Double> getCurrencyRates() {
        return currencyRates;
    }
}

