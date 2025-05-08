package faang.school.paymentservice.service.currency.interfaces;

import faang.school.paymentservice.dto.ExchangeRateResponse;

public interface ExchangeRateProvider {
    ExchangeRateResponse getExchangeRates();
}
