package faang.school.paymentservice.service.currency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.CurrencyRatesDto;
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
            String strResponse = objectMapper.writeValueAsString(jsonResponse);
            customRedisTemplateObject.opsForValue().set(conversionRatesFieldName, strResponse);
            log.info("Currency rates saved to Redis: {}", strResponse);
        } catch (JsonProcessingException e) {
            log.error("Error while saving currency rates to Redis", e);
        }
    }

    public CurrencyRatesDto getAllCurrencyRates() {
        String rates = (String) customRedisTemplateObject.opsForValue().get(conversionRatesFieldName);
        try {
            return objectMapper.readValue(rates, CurrencyRatesDto.class);
        } catch (JsonProcessingException e) {
            log.error("Error while getting currency rates from Redis", e);
            return new CurrencyRatesDto();
        }
    }
}
