package faang.school.paymentservice.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.math.BigDecimal;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisTemplate<String, BigDecimal> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, BigDecimal> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<>(BigDecimal.class));
        return template;
    }
}
