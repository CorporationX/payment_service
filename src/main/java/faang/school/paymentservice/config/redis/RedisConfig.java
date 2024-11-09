package faang.school.paymentservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.dto.event.dmsevent.DmsEventDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {

    private final ObjectMapper objectMapper;

    @Bean
    public JedisConnectionFactory jedisConnectionFactory(
        @Value("${spring.data.redis.host}") String host,
        @Value("${spring.data.redis.port}") Integer port
    ) {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        return new JedisConnectionFactory(config);
    }

    @Bean("dmsTopic")
    public ChannelTopic dmsTopic(
        @Value("${spring.data.redis.channels.dms-channel.name}") String name
    ) {
        return new ChannelTopic(name);
    }

    @Bean("dmsEventRedisTemplate")
    public RedisTemplate<String, DmsEventDto> dmsEventRedisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, DmsEventDto> template = new RedisTemplate<>();
        template.setConnectionFactory(jedisConnectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, DmsEventDto.class));
        return template;
    }
}
