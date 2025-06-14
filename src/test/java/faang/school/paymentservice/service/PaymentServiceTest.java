package faang.school.paymentservice.service;

import faang.school.paymentservice.config.ConverterClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.CurrencyExchangeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private ConverterClient converterClient;

    @InjectMocks
    private PaymentService service;

    @BeforeEach
    void setUp(){
        ReflectionTestUtils.setField(service, "appId", "0134aec31a134dec86124871d5fc0cbf");
    }

    @Test
    void convertToRUB() {
        PaymentRequest dto = new PaymentRequest(1, new BigDecimal("5"), Currency.USD);
        BigDecimal rub = new BigDecimal("397.94");
        HashMap<String, Double> map = new HashMap<>();
        map.put("RUB", 78.8);
        CurrencyExchangeResponse response = new CurrencyExchangeResponse("USD", map);
        String appId = "0134aec31a134dec86124871d5fc0cbf";

        when(converterClient.getCurrentCurrencyExchangeRate(appId, dto.currency().name(), "RUB"))
                .thenReturn(response);

        BigDecimal result = service.convertToRUB(dto);
        assertNotNull(result);
        assertEquals(Double.parseDouble(rub.toString()), Double.parseDouble(result.toString()),1);
    }
}