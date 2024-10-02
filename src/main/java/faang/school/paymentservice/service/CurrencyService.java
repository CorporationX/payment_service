package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeRates;
import faang.school.paymentservice.dto.ExchangeRatesDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class CurrencyService {
    private final ExchangeRates exchangeRates;

    public void currencyRateFetcher() {
//        a9a4c62d6dbea49c6ea892287f57fc24
        ExchangeRatesDto exchangeRatesDto = exchangeRates.fetchData().block();
        if (exchangeRatesDto != null) {
            System.out.println(exchangeRatesDto.getBase());
            System.out.println(exchangeRatesDto.getDate());
            System.out.println(exchangeRatesDto.getTimestamp());
            System.out.println(exchangeRatesDto.getSuccess());
            System.out.println(exchangeRatesDto.getRates());
        }
    }
}
