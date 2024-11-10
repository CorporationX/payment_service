package faang.school.paymentservice.listener.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.CheckingPaymentStatusAndBalance;
import faang.school.paymentservice.service.CheckingBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionalOnProperty(prefix = "app", name = "messaging", havingValue = "redis")
@Component
public class AuthPaymentResponseListener extends AbstractEventListener<CheckingPaymentStatusAndBalance> {
    private final CheckingBalanceService checkingBalanceService;

    public AuthPaymentResponseListener(ObjectMapper objectMapper,
                                       @Value("${spring.data.redis.channel.auth-payment-response}") String topic,
                                       CheckingBalanceService checkingBalanceService) {
        super(objectMapper, new ChannelTopic(topic));
        this.checkingBalanceService = checkingBalanceService;
    }

    @Override
    public void saveEvent(CheckingPaymentStatusAndBalance event) {
        checkingBalanceService.checkBalance(event, event.getStatus());
    }

    @Override
    public Class<CheckingPaymentStatusAndBalance> getEventType() {
        return CheckingPaymentStatusAndBalance.class;
    }

    @Override
    public void handleException(Exception exception) {
        log.error("Error processing auth-payment-response event", exception);
    }
}
