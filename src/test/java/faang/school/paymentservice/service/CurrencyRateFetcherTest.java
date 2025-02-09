package faang.school.paymentservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
public class CurrencyRateFetcherTest {

    @Mock
    CurrencyService currencyService;
    @Spy
    ObjectMapper objectMapper;
    @InjectMocks
    CurrencyRateFetcher currencyRateFetcher;
    private String response;

    @BeforeEach
    public void init(){
        response = "{\"success\":true,\"timestamp\":1739049855,\"base\":\"EUR\",\"date\":\"2025-02-08\",\"rates\":{\"AED\":3.795051,\"AFN\":76.905252,\"ALL\":98.798207,\"AMD\":414.66656,\"ANG\":1.870598,\"AOA\":943.848309,\"ARS\":1093.833705,\"AUD\":1.647875,\"AWG\":1.862375,\"AZN\":1.760572,\"BAM\":1.955171,\"BBD\":2.095626,\"BDT\":126.56927,\"BGN\":1.952062,\"BHD\":0.391274,\"BIF\":3072.711203,\"BMD\":1.033218,\"BND\":1.401749}}";
    }

    @Test
    public void testGetCurrencyExchangeRatesSuccess(){
        Mockito.when(currencyService.getCurrencyExchangeRates()).thenReturn(Mono.fromSupplier(() -> response));

        currencyRateFetcher.getCurrencyExchangeRates();

        Assertions.assertNotNull(currencyRateFetcher.getCurrencyRates());
        System.out.println(currencyRateFetcher.getCurrencyRates());
    }
}
