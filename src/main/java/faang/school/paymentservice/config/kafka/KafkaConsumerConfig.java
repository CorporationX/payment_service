package faang.school.paymentservice.config.kafka;

import faang.school.paymentservice.dto.pending.PendingResponseDto;
import faang.school.paymentservice.dto.pending.RequestOpenDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class KafkaConsumerConfig {

    @Value("${kafka.host}")
    private String host;

    // Общие настройки для всех фабрик
    public Map<String, Object> getCommonConsumerProperties() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, host);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        return props;
    }

    @Bean
    public ConsumerFactory<String, PendingResponseDto> pendingResponceConsumerFactory(){
        Map<String, Object> props = getCommonConsumerProperties();

        JsonDeserializer<PendingResponseDto> deserializer = new JsonDeserializer<>(PendingResponseDto.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, PendingResponseDto> pendingResponseKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, PendingResponseDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(pendingResponceConsumerFactory());
        return factory;
    }

    @Bean
    public ConsumerFactory<String, RequestOpenDto> requestOpenConsumerFactoryConsumerFactory(){
        Map<String, Object> props = getCommonConsumerProperties();

        JsonDeserializer<RequestOpenDto> deserializer = new JsonDeserializer<>(RequestOpenDto.class);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), deserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, RequestOpenDto> requestOpenKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, RequestOpenDto> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(requestOpenConsumerFactoryConsumerFactory());
        return factory;
    }
}
