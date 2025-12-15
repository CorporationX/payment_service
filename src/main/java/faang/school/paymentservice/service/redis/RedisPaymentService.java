package faang.school.paymentservice.service.redis;

import faang.school.paymentservice.dto.AuthorizationDto;
import faang.school.paymentservice.exception.DuplicateRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
public class RedisPaymentService {

    private final RedisService redisService;

    @Value("${app.request-ttl}")
    private Long requestTTL;

    public void validateAuthorizationIdempotence(AuthorizationDto authorizationDto) {
        String  key = String.valueOf(authorizationDto.hashCode());

        if (redisService.exists(key)) {
            throw new DuplicateRequestException("Duplicate request");
        }

        redisService.set(key, authorizationDto, requestTTL, TimeUnit.SECONDS);
    }
}
