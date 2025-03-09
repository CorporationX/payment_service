package faang.school.paymentservice.repository;

import faang.school.paymentservice.entity.Payment;
import faang.school.paymentservice.service.MD5HashService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Repository
public class PaymentHashRepository {
    private final RedisTemplate<String, Object> redisTemplate;
    private final MD5HashService md5HashService;
    @Value("${spring.data.redis.authorization-message.key-name}")
    private String authorizationMessageRedisKey;
    @Value("${spring.data.redis.cancel-message.key-name}")
    private String cancelMessageRedisKey;
    @Value("${spring.data.redis.clearing-message.key-name}")
    private String clearingMessageRedisKey;
    @Value("${payment.valid-interval-minutes}")
    private int paymentsValidIntervalInMinutes = 2;

    public PaymentHashRepository(@Qualifier(value = "paymentHashRedisTemplate") RedisTemplate<String, Object> redisTemplate,
                                 MD5HashService md5HashService) {
        this.redisTemplate = redisTemplate;
        this.md5HashService = md5HashService;
    }

    public void incrementAuthMessageCount(UUID uuid) {
        redisTemplate.opsForHash().increment(authorizationMessageRedisKey, uuid.toString(), 1);
    }

    public int getAuthMessageCount(UUID uuid) {
        Integer count = (Integer) redisTemplate.opsForHash().get(authorizationMessageRedisKey, uuid.toString());
        return Optional.ofNullable(count).orElse(0);
    }

    public void incrementClearingMessageCount(UUID uuid) {
        redisTemplate.opsForHash().increment(clearingMessageRedisKey, uuid.toString(), 1);
    }

    public int getClearingMessageCount(UUID uuid) {
        Integer count = (Integer) redisTemplate.opsForHash().get(clearingMessageRedisKey, uuid.toString());
        return Optional.ofNullable(count).orElse(0);
    }

    public void incrementCancelMessageCount(UUID uuid) {
        redisTemplate.opsForHash().increment(cancelMessageRedisKey, uuid.toString(), 1);
    }

    public int getCancelMessageCount(UUID uuid) {
        Integer count = (Integer) redisTemplate.opsForHash().get(cancelMessageRedisKey, uuid.toString());
        return Optional.ofNullable(count).orElse(0);
    }

    public void deleteAuthMessageCount(UUID uuid) {
        redisTemplate.opsForHash().delete(authorizationMessageRedisKey, uuid.toString());
    }

    public void deleteClearingMessageCount(UUID uuid) {
        redisTemplate.opsForHash().delete(clearingMessageRedisKey, uuid.toString());
    }

    public void deleteCancelMessageCount(UUID uuid) {
        redisTemplate.opsForHash().delete(cancelMessageRedisKey, uuid.toString());
    }

    public void deleteAll() {
        Set<String> keys = redisTemplate.keys("*");
        if (!keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public void insertPaymentHash(Payment payment) {
        String hash = md5HashService.getPaymentHash(payment);
        redisTemplate.opsForValue().set(hash, payment.getCreatedAt().toString(), paymentsValidIntervalInMinutes, TimeUnit.MINUTES);
    }

    public boolean isNewPayment(Payment payment) {
        String hash = md5HashService.getPaymentHash(payment);
        String dateTimeString = (String) redisTemplate.opsForValue().get(hash);
        return Optional.ofNullable(dateTimeString).isEmpty();
    }
}
