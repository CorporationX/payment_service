package faang.school.paymentservice.service;

public interface CurrencyService {
    void fetchAndStoreRates();
    Double getRate(String currencyCode);
}
