package faang.school.paymentservice.validator;

import faang.school.paymentservice.dto.Currency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyValidator {

    public void checkExistCurrency(Currency fromCurrency, Currency toCurrency, Map<String, Double> rates) {
        if (!rates.containsKey(fromCurrency.name()) || !rates.containsKey(toCurrency.name())) {
            log.error("Нет данных для пары валют {} и {}", fromCurrency.name(), toCurrency.name());
            throw new HttpMessageNotReadableException("Currency not");
        }
    }
}