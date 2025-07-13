package faang.school.paymentservice.config.kafka;

import faang.school.paymentservice.event.transfer.CancelTransferFailEventResponse;
import faang.school.paymentservice.event.transfer.CancelTransferSuccessEventResponse;
import faang.school.paymentservice.event.transfer.ClearingTransferFailEventResponse;
import faang.school.paymentservice.event.transfer.ClearingTransferSuccessEventResponse;
import faang.school.paymentservice.event.transfer.TransferFailEventResponse;
import faang.school.paymentservice.event.transfer.TransferSuccessEventResponse;
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
    public ConcurrentKafkaListenerContainerFactory<String, ClearingTransferSuccessEventResponse> kafkaClearTransferSuccessEventListener() {
        return concurrentKafkaListenerJsonFactory(ClearingTransferSuccessEventResponse.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CancelTransferSuccessEventResponse> kafkaCancelTransferSuccessEventListener() {
        return concurrentKafkaListenerJsonFactory(CancelTransferSuccessEventResponse.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransferSuccessEventResponse> kafkaTransferSuccessEventListener() {
        return concurrentKafkaListenerJsonFactory(TransferSuccessEventResponse.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, ClearingTransferFailEventResponse> kafkaClearTransferFailEventListener() {
        return concurrentKafkaListenerJsonFactory(ClearingTransferFailEventResponse.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, CancelTransferFailEventResponse> kafkaCancelTransferFailEventListener() {
        return concurrentKafkaListenerJsonFactory(CancelTransferFailEventResponse.class);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, TransferFailEventResponse> kafkaTransferFailEventListener() {
        return concurrentKafkaListenerJsonFactory(TransferFailEventResponse.class);
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
