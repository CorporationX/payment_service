package faang.school.paymentservice.publisher.payment;

import faang.school.paymentservice.event.payment.PaymentEvent;
import faang.school.paymentservice.model.Currency;
import faang.school.paymentservice.model.Payment;
import faang.school.paymentservice.model.PaymentCategory;
import faang.school.paymentservice.model.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentEventPublisherTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ChannelTopic paymentAuthPendingTopic;

    @Mock
    private ChannelTopic paymentConfirmPendingTopic;

    @Mock
    private ChannelTopic paymentCancelPendingTopic;

    @Mock
    private ChannelTopic paymentClearPendingTopic;

    @InjectMocks
    private PaymentEventPublisher paymentEventPublisher;

    private Payment payment;

    @BeforeEach
    void setUp() {
        payment = new Payment();
        payment.setId(UUID.randomUUID());
        payment.setSourceAccountId(UUID.randomUUID());
        payment.setTargetAccountId(UUID.randomUUID());
        payment.setAmount(BigDecimal.valueOf(100.00));
        payment.setCurrency(Currency.USD);
        payment.setCategory(PaymentCategory.PREMIUM_SUBSCRIPTION);
    }

    @Test
    @DisplayName("Publishes event to AUTH_PENDING topic")
    void whenStatusIsAuthPendingThenPublishesToAuthPendingTopic() {
        payment.setStatus(PaymentStatus.AUTH_PENDING);

        paymentEventPublisher.publishPayment(payment);

        verify(redisTemplate).convertAndSend(any(), any(PaymentEvent.class));
    }

    @Test
    @DisplayName("Publishes event to CONFIRM_PENDING topic")
    void whenStatusIsConfirmPendingThenPublishesToConfirmPendingTopic() {
        payment.setStatus(PaymentStatus.CONFIRM_PENDING);

        paymentEventPublisher.publishPayment(payment);

        verify(redisTemplate).convertAndSend(any(), any(PaymentEvent.class));
    }

    @Test
    @DisplayName("Publishes event to CANCEL_PENDING topic")
    void whenStatusIsCancelPendingThenPublishesToCancelPendingTopic() {
        payment.setStatus(PaymentStatus.CANCEL_PENDING);

        paymentEventPublisher.publishPayment(payment);

        verify(redisTemplate).convertAndSend(any(), any(PaymentEvent.class));
    }

    @Test
    @DisplayName("Publishes event to CLEAR_PENDING topic")
    void whenStatusIsClearPendingThenPublishesToClearPendingTopic() {
        payment.setStatus(PaymentStatus.CLEAR_PENDING);

        paymentEventPublisher.publishPayment(payment);

        verify(redisTemplate).convertAndSend(any(), any(PaymentEvent.class));
    }

    @Test
    @DisplayName("Throws exception for unsupported payment status")
    void whenStatusIsUnsupportedThenThrowsException() {
        payment.setStatus(PaymentStatus.AUTH_SUCCESS);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> paymentEventPublisher.publishPayment(payment)
        );

        assertEquals("Unsupported payment status: AUTH_SUCCESS", exception.getMessage());
    }
}