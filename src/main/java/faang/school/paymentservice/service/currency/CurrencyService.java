package faang.school.paymentservice.service.currency;

import java.math.BigDecimal;

public interface CurrencyService {

    String getCurrencyRate();

    void clearRates();

    BigDecimal getRate(String currency);
}