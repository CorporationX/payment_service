package faang.school.paymentservice.service.currency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.config.currencyRate.CurrencyRateFetcherConfig;
import faang.school.paymentservice.dto.CurrencyDto;
import faang.school.paymentservice.dto.ValuteInfo;
import faang.school.paymentservice.exception.JsonParsingException;
import faang.school.paymentservice.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CurrencyServiceImplTest {

    @Mock
    private CurrencyRateFetcherConfig fetcherConfig;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    @Test
    public void getCurrencyRate_ValidData_shouldReturnCorrectCurrencyRates() {
        String json = "{\"Valute\":{\"USD\":{\"Nominal\":1,\"Value\":95.5}}}";
        Map<String, ValuteInfo> valuteMap = new HashMap<>();
        valuteMap.put("USD", new ValuteInfo(1, new BigDecimal("95.5")));
        CurrencyDto currencyDto = new CurrencyDto(valuteMap);

        when(fetcherConfig.getCurrentRate()).thenReturn(json);
        try {
            when(objectMapper.readValue(json, CurrencyDto.class)).thenReturn(currencyDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        Map<String, BigDecimal> result = currencyService.getCurrencyRate();

        assertEquals(2, result.size());
        assertEquals(0, new BigDecimal("95.5").compareTo(result.get("USD")));
        assertEquals(BigDecimal.ONE, result.get("RUB"));
    }

    @Test
    public void getCurrencyRate_InvalidJson_shouldThrowJsonParsingException() {
        String json = "invalid json";

        when(fetcherConfig.getCurrentRate()).thenReturn(json);
        try {
            when(objectMapper.readValue(json, CurrencyDto.class)).thenThrow(new RuntimeException());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        assertThrows(JsonParsingException.class, () -> currencyService.getCurrencyRate());
    }

    @Test
    public void getCurrencyRate_nullValute_shouldThrowNotFoundException() {
        String json = "{}";
        CurrencyDto currencyDto = new CurrencyDto(null);

        when(fetcherConfig.getCurrentRate()).thenReturn(json);
        try {
            when(objectMapper.readValue(json, CurrencyDto.class)).thenReturn(currencyDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        assertThrows(NotFoundException.class, () -> currencyService.getCurrencyRate());
    }
}