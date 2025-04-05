package faang.school.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

public record CurrencyResponse(
        @JsonProperty("success") boolean success,
        @JsonProperty("timestamp") long timestamp,
        @JsonProperty("base") String base,
        @JsonProperty("date") String date,
        @JsonProperty("rates") Map<String, Double> rates) {
}