package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.PaymentRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    PaymentService service = new PaymentService();

    @Test
    void convertToRUB() {
        PaymentRequest dto = new PaymentRequest(1, new BigDecimal("5"), Currency.USD);
        BigDecimal rub = new BigDecimal("397.94");

        BigDecimal result = service.convertToRUB(dto);
        assertNotNull(result);
        assertEquals(Double.parseDouble(rub.toString()), Double.parseDouble(result.toString()),1);
    }
}