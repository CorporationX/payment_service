package faang.school.paymentservice.service.currency;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.config.currencyRate.CurrencyRateFetcherConfig;
import faang.school.paymentservice.dto.CurrencyDto;
import faang.school.paymentservice.exception.JsonParsingException;
import faang.school.paymentservice.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class CurrencyServiceImpl implements CurrencyService {
    private final CurrencyRateFetcherConfig fetcherConfig;
    private final ObjectMapper objectMapper;

    @Override
    public Map<String, BigDecimal> getCurrencyRate() {
        String json = fetcherConfig.getCurrentRate();
        log.info("Load to Currency rates length json {}", json.length());

        CurrencyDto root;
        try {
            root = objectMapper.readValue(json, CurrencyDto.class);
        } catch (Exception e) {
            log.error("Failed to parse currency rate JSON", e);
            throw new JsonParsingException("Error json parsing current rate");
        }
        if (root == null || root.valute() == null) {
            throw new NotFoundException("Empty or invalid currency response");
        }

        Map<String, BigDecimal> rates = new HashMap<>();
        root.valute().forEach((code, info) -> {
            BigDecimal rate = info.value().divide(BigDecimal.valueOf(info.nominal()), 10, RoundingMode.HALF_UP);
            rates.put(code, rate);
        });
        rates.put("RUB", BigDecimal.ONE);
        log.info("Successfully loaded {} currency rates", rates.size());
        return rates;
    }
}