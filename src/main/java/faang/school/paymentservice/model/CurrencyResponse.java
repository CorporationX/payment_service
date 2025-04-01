package faang.school.paymentservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public class CurrencyResponse {
    private boolean success;
    private long timestamp;
    private String base;
    private String date;
    private Map<String, Double> rates;

    @JsonProperty("success")
    public boolean isSuccess() {
        return success;
    }

    @JsonProperty("timestamp")
    public long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("base")
    public String getBase() {
        return base;
    }

    @JsonProperty("date")
    public String getDate() {
        return date;
    }

    @JsonProperty("rates")
    public Map<String, Double> getRates() {
        return rates;
    }
}



