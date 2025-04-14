package faang.school.paymentservice.service.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.CurrencyDto;
import faang.school.paymentservice.dto.PaymentRequestDto;
import faang.school.paymentservice.dto.PaymentResponseDto;
import faang.school.paymentservice.dto.PaymentStatus;
import faang.school.paymentservice.dto.exchange.ExchangeRequestDto;
import faang.school.paymentservice.dto.exchange.ExchangeResponseDto;
import faang.school.paymentservice.dto.premium.PremiumPaymentRequestDto;
import faang.school.paymentservice.dto.premium.PremiumPaymentResponseDto;
import faang.school.paymentservice.dto.premium.PremiumRequestDto;
import faang.school.paymentservice.enums.PremiumType;
import faang.school.paymentservice.service.kafka.listener.PremiumPaymentListener;
import faang.school.paymentservice.service.kafka.publisher.KafkaPublisher;
import faang.school.paymentservice.service.payment.PaymentServiceImpl;
import faang.school.paymentservice.utils.JsonUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PremiumPaymentListenerTest {
    @InjectMocks
    private PremiumPaymentListener premiumPaymentListener;

    @Mock
    private PaymentServiceImpl paymentService;

    @Mock
    private JsonUtils jsonUtils;

    @Mock
    private KafkaPublisher kafkaPublisher;

    @Mock
    private Acknowledgment acknowledgment;

    private PremiumRequestDto premiumRequest;
    private PaymentRequestDto paymentRequest;
    private PremiumPaymentRequestDto premiumPaymentRequest;
    private PaymentResponseDto paymentResponse;
    private ExchangeRequestDto exchangeRequest;
    private ExchangeResponseDto exchangeResponse;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String paymentResponseTopic = "topic1";
    private final String priceResponseTopic = "topic2";
    private final String premiumPaymentCorrelationId = "correlation-id-1";
    private final String correlationId = "correlation-1";
    private final String premiumPriceCorrelationId = "correlation-id-2";

    @BeforeEach
    public void setUp() {
        premiumRequest = new PremiumRequestDto(PremiumType.ONE_MONTH, 1L, CurrencyDto.USD, true);
        paymentRequest = new PaymentRequestDto(1L, BigDecimal.TEN, CurrencyDto.USD);
        premiumPaymentRequest = new PremiumPaymentRequestDto(premiumRequest,
                paymentRequest, CurrencyDto.USD, true);

        exchangeRequest = new ExchangeRequestDto(CurrencyDto.EUR, CurrencyDto.USD, BigDecimal.TEN, 1L);
        exchangeResponse = new ExchangeResponseDto(exchangeRequest.getToCurrency(),
                BigDecimal.TEN, exchangeRequest.getUserId());

        paymentResponse = new PaymentResponseDto(PaymentStatus.SUCCESS, 1, 1L,
                BigDecimal.TEN, CurrencyDto.USD, "message");

        ReflectionTestUtils.setField(premiumPaymentListener, "paymentResponseTopic", paymentResponseTopic);
        ReflectionTestUtils.setField(premiumPaymentListener, "premiumPaymentCorrelationId", premiumPaymentCorrelationId);
        ReflectionTestUtils.setField(premiumPaymentListener, "premiumPriceCorrelationId", premiumPriceCorrelationId);
        ReflectionTestUtils.setField(premiumPaymentListener, "priceResponseTopic", priceResponseTopic);
    }

    @Test
    public void testPremiumPaymentRequestListener_success() throws Exception {
        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0L,
                "key", objectMapper.writeValueAsString(premiumPaymentRequest));

        when(jsonUtils.deserialize(record.value(), PremiumPaymentRequestDto.class)).thenReturn(premiumPaymentRequest);
        when(paymentService.sendPayment(paymentRequest)).thenReturn(ResponseEntity.ok(paymentResponse));
        record.headers().add(new RecordHeader(premiumPaymentCorrelationId,
                correlationId.getBytes(StandardCharsets.UTF_8)));

        premiumPaymentListener.premiumPaymentRequestListener(record, acknowledgment);

        verify(kafkaPublisher, times(1)).sendInTransaction(any(PremiumPaymentResponseDto.class),
                eq(paymentResponseTopic), eq(premiumPaymentCorrelationId), eq(correlationId));
    }

    @Test
    public void testPremiumPriceRequestListener_success() throws Exception {
        ConsumerRecord<String, String> record = new ConsumerRecord<>("topic", 0, 0L,
                "key", objectMapper.writeValueAsString(premiumPaymentRequest));

        when(jsonUtils.deserialize(record.value(), ExchangeRequestDto.class)).thenReturn(exchangeRequest);
        when(paymentService.convertCurrency(exchangeRequest)).thenReturn(exchangeResponse);
        record.headers().add(new RecordHeader(premiumPriceCorrelationId,
                correlationId.getBytes(StandardCharsets.UTF_8)));

        premiumPaymentListener.premiumPriceRequestListener(record, acknowledgment);

        verify(kafkaPublisher, times(1)).sendInTransaction(any(ExchangeResponseDto.class),
                eq(priceResponseTopic), eq(premiumPriceCorrelationId), eq(correlationId));
    }
}
