package faang.school.paymentservice.config.transfer;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "clearing.transfers")
@Getter
@Setter
@Component
public class ClearTransferConfig {
    private String cron;
    private int batchSize;
    private int threadPoolSize;
    private int terminationAwait;
}
