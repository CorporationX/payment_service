package faang.school.paymentservice.config.async;

import faang.school.paymentservice.properties.async.RequestSenderAsyncProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("requestSender")
@RequiredArgsConstructor
public class RequestSenderExecutorConfig extends AbstractExecutorConfig {

    public RequestSenderExecutorConfig(RequestSenderAsyncProperties properties) {
        createThreadPool(properties);
    }
}
