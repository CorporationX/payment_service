package faang.school.paymentservice.service.rates;

import java.util.Map;

public interface CurrencyFetchService {

    Map<String, Double> fetch();
}