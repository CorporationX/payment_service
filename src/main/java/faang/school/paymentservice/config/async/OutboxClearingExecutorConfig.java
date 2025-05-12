package faang.school.paymentservice.config.async;

import faang.school.paymentservice.properties.async.OutboxClearingAsyncProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("outboxClearing")
@RequiredArgsConstructor
public class OutboxClearingExecutorConfig extends AbstractExecutorConfig {

    public OutboxClearingExecutorConfig(OutboxClearingAsyncProperties properties) {
        createThreadPool(properties);
    }
}
