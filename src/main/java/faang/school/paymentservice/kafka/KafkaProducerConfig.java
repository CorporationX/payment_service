package faang.school.paymentservice.kafka;

import faang.school.paymentservice.model.dto.PaymentMessageDto;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.transaction.KafkaTransactionManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Конфигурация Kafka Producer для PaymentService.
 * <p>
 * Здесь настраивается:
 * <ul>
 *     <li>{@link KafkaTemplate} — шаблон для отправки сообщений в Kafka;</li>
 *     <li>{@link ProducerFactory} — фабрика для создания Kafka Producer;</li>
 *     <li>{@link KafkaTransactionManager} — менеджер транзакций для интеграции с Spring Transaction Management.</li>
 * </ul>
 * <p>
 * Настройки producer:
 * <ul>
 *     <li><b>bootstrap.servers</b> — адреса Kafka брокеров, берется из application.yaml;</li>
 *     <li><b>key.serializer</b> — сериализатор ключей сообщений (StringSerializer);</li>
 *     <li><b>value.serializer</b> — сериализатор значений сообщений (JsonSerializer для PaymentMessageDto);</li>
 *     <li><b>acks=all</b> — подтверждение доставки до всех ISR;</li>
 *     <li><b>retries=3</b> — повторная попытка отправки при временной ошибке;</li>
 *     <li><b>linger.ms=5</b> — задержка перед отправкой, для группировки сообщений;</li>
 *     <li><b>enable.idempotence=true</b> — включение идемпотентного продюсера;</li>
 *     <li><b>transactional.id=payment-tx-producer</b> — уникальный идентификатор транзакционного продюсера.</li>
 * </ul>
 * <p>
 * Использование {@link KafkaTransactionManager} позволяет интегрировать Kafka-транзакции с JPA транзакциями
 * через {@link org.springframework.transaction.PlatformTransactionManager}, например, с {@link org.springframework.orm.jpa.JpaTransactionManager}.
 */
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaTemplate<String, PaymentMessageDto> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    /**
     * Менеджер транзакций для Kafka Producer.
     * Используется для интеграции Kafka-транзакций с Spring @Transactional.
     *
     * @param producerFactory фабрика продюсеров
     * @return KafkaTransactionManager
     */
    @Bean
    public KafkaTransactionManager<String, PaymentMessageDto> kafkaTransactionManager(
            ProducerFactory<String, PaymentMessageDto> producerFactory) {
        return new KafkaTransactionManager<>(producerFactory);
    }

    /**
     * Фабрика продюсеров Kafka с настройками безопасности, идемпотентности и транзакций.
     *
     * @return ProducerFactory
     */
    @Bean
    public ProducerFactory<String, PaymentMessageDto> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false);

        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        config.put(ProducerConfig.LINGER_MS_CONFIG, 5);
        config.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, true);

        config.put(ProducerConfig.TRANSACTIONAL_ID_CONFIG, "payment-tx-producer");

        return new DefaultKafkaProducerFactory<>(config);
    }
}