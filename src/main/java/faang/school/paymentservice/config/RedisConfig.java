package faang.school.paymentservice.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

@Setter
@Getter
@RequiredArgsConstructor
@ConfigurationProperties("spring.data.redis")
@Configuration
public class RedisConfig {
    private int port;
    private String host;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(){
        RedisStandaloneConfiguration redisConfig = new RedisStandaloneConfiguration();
        redisConfig.setPort(port);
        redisConfig.setHostName(host);
        return new JedisConnectionFactory(redisConfig);
    }

    @Bean(name = "currencyRatesRedisTemplate")
    public StringRedisTemplate redisCurrencyRates(){
        StringRedisTemplate redisTemplate = new StringRedisTemplate();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }
}
