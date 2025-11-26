package faang.school.paymentservice.dto;

import java.util.Map;

public class ExchangeRatesResponse {
    private boolean success;
    private long timestamp;
    private String base;
    private String date;
    private Map<String, Double> rates;
}
