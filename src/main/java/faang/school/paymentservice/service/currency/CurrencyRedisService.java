package faang.school.paymentservice.service.currency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class CurrencyRedisService {
    private final RedisTemplate<String, Object> customRedisTemplateObject;
    private final ObjectMapper objectMapper;
    private final String conversionRatesFieldName;

    public void saveCurrencyRates(Map<String, Object> jsonResponse) {
        try {
            String rates = objectMapper.writeValueAsString(jsonResponse.get(conversionRatesFieldName));
            customRedisTemplateObject.opsForValue().set(conversionRatesFieldName, rates);
            log.info("Currency rates saved to Redis: {}", rates);
        } catch (JsonProcessingException e) {
            log.error("Error while saving currency rates to Redis", e);
        }
    }

    public Map<String, Object> getAllCurrencyRates() {
        String rates = (String) customRedisTemplateObject.opsForValue().get(conversionRatesFieldName);
        try {
            return objectMapper.readValue(rates, new TypeReference<Map<String, Object>>() {
            });
        } catch (JsonProcessingException e) {
            log.error("Error while getting currency rates from Redis", e);
            return Collections.emptyMap();
        }
    }
}
