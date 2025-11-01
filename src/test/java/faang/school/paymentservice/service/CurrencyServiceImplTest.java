package faang.school.paymentservice.service;

import faang.school.paymentservice.config.CurrencyRateFetcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {

    @Mock
    private CurrencyRateFetcher currencyRateFetcher;
    @InjectMocks
    CurrencyServiceImpl currencyService;


    @Test
    public void getCurrencyRate_responseString_shouldResponseString(){
        Map<String, Double> map = new HashMap<>();
        map.put("EUR", 1.111);
        map.put("RUB", 2.222);
        when(currencyRateFetcher.getMapCurrentRate()).thenReturn(map);

        String currentCurrency = currencyService.getCurrencyRate();
        List<String> lines = currentCurrency.lines().toList();

        assertEquals("EUR-1.111000", lines.get(0));
        assertEquals("RUB-2.222000", lines.get(1));
    }
}