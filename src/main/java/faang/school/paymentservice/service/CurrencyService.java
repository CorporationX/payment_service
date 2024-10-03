package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ExchangeRates;
import faang.school.paymentservice.dto.ExchangeRatesDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CurrencyService {
    private final ExchangeRates exchangeRates;

    public void currencyRateFetcher() {
        System.out.println(11111111);
        ExchangeRatesDto exchangeRatesDto = exchangeRates.fetchData().block();
        System.out.println(exchangeRatesDto);
//        exchangeRates.fetchData()
//                .subscribe
//                (
//                        result -> System.out.println("Полученные данные: " + result),
//                        error -> System.out.println("Произошла ошибка: " + error.getMessage()),
//                        () -> System.out.println("Завершение обработки запроса")
//                );
//

        if (exchangeRatesDto != null && exchangeRatesDto.getSuccess().equalsIgnoreCase("true")) {
            System.out.println(exchangeRatesDto.getBase());
            System.out.println(exchangeRatesDto.getDate());
            System.out.println(exchangeRatesDto.getTimestamp());
            System.out.println(exchangeRatesDto.getSuccess());
            System.out.println(exchangeRatesDto.getRates());
        } else {
            System.out.println("somethin happened");;
        }
    }
}
