package faang.school.paymentservice.config.kafka;

import faang.school.paymentservice.event.CancelTransferEventRequest;
import faang.school.paymentservice.event.ClearingTransferEventRequest;
import faang.school.paymentservice.event.TransferEventRequest;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    private final KafkaProperties kafkaProperties;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ClearingTransferEventRequest> kafkaClearTransferSuccessEventListener() {
        return concurrentKafkaListenerJsonFactory(ClearingTransferEventRequest.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CancelTransferEventRequest> kafkaCancelTransferSuccessEventListener() {
        return concurrentKafkaListenerJsonFactory(CancelTransferEventRequest.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransferEventRequest> kafkaTransferSuccessEventListener() {
        return concurrentKafkaListenerJsonFactory(TransferEventRequest.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ClearingTransferEventRequest> kafkaClearTransferFailEventListener() {
        return concurrentKafkaListenerJsonFactory(ClearingTransferEventRequest.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CancelTransferEventRequest> kafkaCancelTransferFailEventListener() {
        return concurrentKafkaListenerJsonFactory(CancelTransferEventRequest.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransferEventRequest> kafkaTransferFailEventListener() {
        return concurrentKafkaListenerJsonFactory(TransferEventRequest.class);
    }

    private <T> ConcurrentKafkaListenerContainerFactory<String, T> concurrentKafkaListenerJsonFactory(Class<T> tClass) {
        Map<String, Object> jsonFactoryConfig = new HashMap<>();
        jsonFactoryConfig.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        jsonFactoryConfig.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaProperties.getConsumer().getGroupId());
        jsonFactoryConfig.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        jsonFactoryConfig.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        jsonFactoryConfig.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        ConcurrentKafkaListenerContainerFactory<String, T> factory = new ConcurrentKafkaListenerContainerFactory<>();

        ConsumerFactory<String, T> consumerFactory = new DefaultKafkaConsumerFactory<>(
                jsonFactoryConfig,
                new StringDeserializer(),
                new JsonDeserializer<>(tClass, false)
        );

        factory.setConsumerFactory(consumerFactory);
        return factory;
    }
}
