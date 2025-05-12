package faang.school.paymentservice.config.async;

import faang.school.paymentservice.properties.async.RequestForceConfirmAsyncProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("requestForceConfirm")
@RequiredArgsConstructor
public class RequestForceConfirmExecutorConfig extends AbstractExecutorConfig {

    public RequestForceConfirmExecutorConfig(RequestForceConfirmAsyncProperties properties) {
        createThreadPool(properties);
    }
}
