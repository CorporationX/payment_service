package faang.school.paymentservice.kafka;

import faang.school.paymentservice.model.dto.PaymentMessageDto;
import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.transaction.ChainedKafkaTransactionManager;
import org.springframework.kafka.transaction.KafkaTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Конфигурация транзакций для PaymentService.
 * <p>
 * Создаёт {@link ChainedKafkaTransactionManager}, который объединяет:
 * <ul>
 *     <li>JPA транзакции (через {@link JpaTransactionManager}) для работы с базой данных;</li>
 *     <li>Kafka транзакции (через {@link KafkaTransactionManager}) для атомарной отправки сообщений в топики Kafka.</li>
 * </ul>
 * <p>
 * Использование {@code ChainedKafkaTransactionManager} позволяет гарантировать:
 * <ul>
 *     <li>Если база данных успешно сохраняет изменения, Kafka-транзакция также будет выполнена;</li>
 *     <li>Если происходит ошибка на любом этапе, все операции откатываются, включая Kafka сообщения.</li>
 * </ul>
 * <p>
 * Бин помечен как {@link Primary} и имеет имя {@code transactionManager}, чтобы Spring
 * мог использовать его по умолчанию при аннотации {@link org.springframework.transaction.annotation.Transactional}.
 */
@Configuration
@EnableTransactionManagement
public class TransactionConfig {

    /**
     * Создаёт комбинированный менеджер транзакций для JPA и Kafka.
     *
     * @param emf          фабрика EntityManager для JPA
     * @param kafkaTxManager менеджер транзакций Kafka
     * @return {@link PlatformTransactionManager}, объединяющий JPA и Kafka транзакции
     */
    @Bean(name = "transactionManager")
    @Primary
    public PlatformTransactionManager chainedTransactionManager(
            EntityManagerFactory emf,
            KafkaTransactionManager<String, PaymentMessageDto> kafkaTxManager) {
        JpaTransactionManager jpaTxManager = new JpaTransactionManager(emf);
        return new ChainedKafkaTransactionManager<>(jpaTxManager, kafkaTxManager);
    }
}