package faang.school.paymentservice.properties.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.data.kafka.topics.payment-operation")
public record PaymentOperationTopicProperties(
        String name,
        int partitions,
        int replicas
) {}
