package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.LatestRatesResponse;
import reactor.core.publisher.Mono;

/**
 * Сервис для работы с курсами валют.
 * Предоставляет функционал для обновления курсов из внешнего источника,
 * получения всех курсов валют по отношению к базовой валюте
 * и получения курса конкретной валюты относительно базовой.
 */
public interface CurrencyService {

    /**
     * Обновляет курсы валют, получая данные от внешнего API, и сохраняет их как кэш в Redis.
     *
     * @return {@link Mono<Void>} завершение операции обновления
     */
    Mono<Void> updateRates();

    /**
     * Получает все актуальные курсы валют из Redis.
     * Если данные отсутствуют в кэше, выполняет их обновление.
     *
     * @return {@link Mono<LatestRatesResponse>} объект с актуальными курсами валют
     */
    Mono<LatestRatesResponse> getAllRates();

    /**
     * Получает курс заданной валюты относительно базовой валюты.
     * Если валюта не найдена, возвращает ошибку.
     *
     * @param toRate код целевой валюты (например, "EUR")
     * @return {@link Mono<Double>} курс указанной валюты
     */
    Mono<Double> getRate(String toRate);

}
