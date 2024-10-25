package faang.school.paymentservice.integration.config;

import com.redis.testcontainers.RedisContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration
public class TestContainersConfig {
    private static final String REDIS_IMAGE = "redis/redis-stack:latest";

    @Container
    public static final RedisContainer redisContainer = new RedisContainer(DockerImageName.parse(REDIS_IMAGE));
}
