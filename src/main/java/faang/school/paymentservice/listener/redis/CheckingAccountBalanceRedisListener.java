package faang.school.paymentservice.listener.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.CheckingAccountBalance;
import faang.school.paymentservice.service.CheckingAccountBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.listener.Topic;
import org.springframework.stereotype.Component;

@Slf4j
@ConditionalOnProperty(prefix = "app", name = "messaging", havingValue = "redis")
@Component
public class CheckingAccountBalanceRedisListener extends AbstractEventListener<CheckingAccountBalance> {
    private final Topic topic;
    private final CheckingAccountBalanceService checkingAccountBalanceService;

    public CheckingAccountBalanceRedisListener(ObjectMapper javaTimeModuleObjectMapper, Topic checkAccountBalanceTopic,
                                               CheckingAccountBalanceService checkingAccountBalanceService) {
        super(javaTimeModuleObjectMapper, checkAccountBalanceTopic);
        this.topic = checkAccountBalanceTopic;
        this.checkingAccountBalanceService = checkingAccountBalanceService;
    }

    @Override
    public void saveEvent(CheckingAccountBalance event) {
        checkingAccountBalanceService.checkBalance(event, event.getStatus());
    }

    @Override
    public Class<CheckingAccountBalance> getEventType() {
        return CheckingAccountBalance.class;
    }

    @Override
    public void handleException(Exception exception) {
        log.error("Unexpected error, listen topic: {}", topic.getTopic(), exception);
        throw new RuntimeException(exception);
    }
}
