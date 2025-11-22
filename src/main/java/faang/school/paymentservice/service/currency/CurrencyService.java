package faang.school.paymentservice.service.currency;

import java.math.BigDecimal;
import java.util.Map;

public interface CurrencyService {

    Map<String, BigDecimal> getCurrencyRate();

}