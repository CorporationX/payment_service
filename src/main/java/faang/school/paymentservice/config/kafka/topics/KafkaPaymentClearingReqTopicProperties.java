package faang.school.paymentservice.config.kafka.topics;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@ConfigurationProperties(prefix = "spring.kafka.topic.payment-clearing-request")
@Configuration
public class KafkaPaymentClearingReqTopicProperties {
    private String name;
    private int partitions;
}
