package faang.school.paymentservice.service.currency;

public interface CurrencyService {

    String getCurrencyRate();

    void clearRates();
}