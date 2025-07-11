package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.config.redis.RedisTtlProperties;
import faang.school.paymentservice.exception.redis.RedisUnavailableException;
import faang.school.paymentservice.model.payment.OperationTokenResult;
import faang.school.paymentservice.model.redis.payment.OperationTokenRedisModel;
import faang.school.paymentservice.repository.payment.OperationTokenRedisRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OperationTokenRedisService {
    private final OperationTokenRedisRepository operationTokenRedisRepository;
    private final RedisTtlProperties redisTtlProperties;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "operation_token:";
    @Retryable(
            retryFor = {RedisConnectionFailureException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public OperationTokenResult saveOperationToken(UUID operationToken) {
        try {
            String key = PREFIX + operationToken;
            LocalDateTime now = LocalDateTime.now();

            // Попытка установить ключ только если его нет
            Boolean isNew = redisTemplate.opsForValue()
                    .setIfAbsent(key, now.toString(), Duration.ofSeconds(redisTtlProperties.getOperationToken()));

            if (Boolean.TRUE.equals(isNew)) {
                log.info("Token {} created in Redis", key);
                return new OperationTokenResult(
                        new OperationTokenRedisModel(operationToken, 15L,  now),
                        false
                );
            }

            // Ключ уже есть — читаем его значение (время создания)
            String timestamp = redisTemplate.opsForValue().get(key);

            if (timestamp == null) {
                // Redis жив, но значение исчезло по какой-то причине
                throw new IllegalStateException("Token key exists but has no value: " + key);
            }

            LocalDateTime createdAt = LocalDateTime.parse(timestamp);
            log.info("Token {} already present in Redis, createdAt = {}", key, createdAt);

            return new OperationTokenResult(
                    new OperationTokenRedisModel(operationToken, 15L, createdAt),
                    true
            );
        } catch (Exception ex) {
            String errorMsg = String.format("Redis unavailable while handling token %s", operationToken);
            log.error(errorMsg, ex);
            throw new RedisUnavailableException(errorMsg);
        }
    }

//    public boolean saveOperationToken(UUID operationToken) {
//        OperationTokenRedisModel operationTokenRedisModel =
//                OperationTokenRedisModel.builder()
//                        .key(operationToken)
//                        .ttl(redisTtlProperties.getOperationToken())
//                        .build();
//        operationTokenRedisRepository.save(operationTokenRedisModel);
//        log.info("Operation token {} has been saved in redis", operationTokenRedisModel);
//
//        // TODO: нужно придумать как возвращать true, если уже существует
//        return false;
//    }
}
