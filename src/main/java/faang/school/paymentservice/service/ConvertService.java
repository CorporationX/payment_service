package faang.school.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConvertService {
    private final RedisCacheManager manager;
}
