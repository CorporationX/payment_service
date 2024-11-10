package faang.school.paymentservice.listener.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.PaymentStatusResponseDto;
import faang.school.paymentservice.service.CheckPaymentStatusService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionalOnProperty(prefix = "app", name = "messaging", havingValue = "redis")
@Component
public class ErrorPaymentResponseListener extends AbstractEventListener<PaymentStatusResponseDto> {
    private final CheckPaymentStatusService checkPaymentStatusService;

    public ErrorPaymentResponseListener(ObjectMapper objectMapper,
                                        @Value("${spring.data.redis.channel.error-payment-response}") String topic,
                                        CheckPaymentStatusService checkPaymentStatusService) {
        super(objectMapper, new ChannelTopic(topic));
        this.checkPaymentStatusService = checkPaymentStatusService;
    }

    @Override
    public void saveEvent(PaymentStatusResponseDto event) {
        checkPaymentStatusService.checkPaymentStatus(event);
    }

    @Override
    public Class<PaymentStatusResponseDto> getEventType() {
        return PaymentStatusResponseDto.class;
    }

    @Override
    public void handleException(Exception exception) {
        log.error("Error processing error-payment-response event", exception);
    }
}
