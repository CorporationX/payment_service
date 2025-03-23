package faang.school.paymentservice.config.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private List<String> bootstrapServers;

    @Value("${spring.kafka.topics.payment-topics.authorization.request-topic-name}")
    private String authRequestTopicName;

    @Value("${spring.kafka.topics.payment-topics.cancel.request-topic-name}")
    private String cancelRequestTopicName;

    @Value("${spring.kafka.topics.payment-topics.clearing.request-topic-name}")
    private String clearingRequestTopicName;

    @Value("${spring.kafka.topics.payment-topics.dlt.topic-name}")
    private String dltTopicName;

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Primary
    @Bean("sendRequestKafkaTemplate")
    public KafkaTemplate<String, Object> sendRequestKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean("dltKafkaTemplate")
    public KafkaTemplate<String, Object> dltKafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean("requestTopicName")
    public Map<String, String> requestTopicName() {
        return Map.of("authRequestTopicName", authRequestTopicName,
                "cancelRequestTopicName", cancelRequestTopicName,
                "clearingRequestTopicName", clearingRequestTopicName,
                "dltTopicName", dltTopicName);
    }

}
