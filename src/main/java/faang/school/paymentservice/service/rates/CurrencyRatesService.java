package faang.school.paymentservice.service.rates;

import java.util.Map;

public interface CurrencyRatesService {

    Map<String, Double> fetch();
}