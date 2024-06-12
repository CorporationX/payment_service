package faang.school.paymentservice.service;

import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Data
public class RatesCache {
    private Map<String, Double> rates = new HashMap<>();
}