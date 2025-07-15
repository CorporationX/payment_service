package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.config.redis.RedisTtlProperties;
import faang.school.paymentservice.exception.redis.RedisUnavailableException;
import faang.school.paymentservice.model.payment.OperationTokenModel;
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
    private final RedisTtlProperties redisTtlProperties;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String PREFIX = "operation_token:";

    @Retryable(
            retryFor = {RedisConnectionFailureException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public OperationTokenModel saveOperationToken(UUID operationToken) {
        try {
            String key = PREFIX + operationToken;
            LocalDateTime now = LocalDateTime.now();

            Boolean isNew = redisTemplate.opsForValue()
                    .setIfAbsent(key, now.toString(), Duration.ofSeconds(redisTtlProperties.getOperationToken()));

            if (Boolean.TRUE.equals(isNew)) {
                log.info("Token {} created in Redis", key);
                return new OperationTokenModel(operationToken, now, false);
            }

            String timestamp = redisTemplate.opsForValue().get(key);

            LocalDateTime createdAt = LocalDateTime.parse(timestamp);
            log.info("Token {} already present in Redis, createdAt = {}", key, createdAt);

            return new OperationTokenModel(operationToken, createdAt, true);
        } catch (Exception ex) {
            String errorMsg = String.format("Redis unavailable while handling token %s", operationToken);
            log.error(errorMsg, ex);
            throw new RedisUnavailableException(errorMsg);
        }
    }
}
