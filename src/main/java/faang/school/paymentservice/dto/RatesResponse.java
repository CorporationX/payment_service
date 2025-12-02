package faang.school.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record RatesResponse(
        String disclaimer,
        String license,
        long timestamp,
        String base,
        Map<String, BigDecimal> rates
) {}