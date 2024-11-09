package faang.school.paymentservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.listener.payment.AuthEventResponseListener;
import faang.school.paymentservice.listener.payment.CancelEventResponseListener;
import faang.school.paymentservice.listener.payment.ForcedEventResponseListener;
import faang.school.paymentservice.listener.payment.ScheduledEventResponseListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.Topic;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfig {
    private final RedisProperties redisProperties;
    private final AuthEventResponseListener authEventResponseListener;
    private final CancelEventResponseListener cancelEventResponseListener;
    private final ForcedEventResponseListener forcedEventResponseListener;
    private final ScheduledEventResponseListener scheduledEventResponseListener;
    private final ObjectMapper objectMapper;


    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(redisProperties.getHost());
        redisStandaloneConfiguration.setPort(redisProperties.getPort());

        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer() {
        RedisMessageListenerContainer redisContainer = new RedisMessageListenerContainer();
        redisContainer.setConnectionFactory(redisConnectionFactory());

        redisContainer.addMessageListener(authEventResponceAdapter(), authEventResponceTopic());
        redisContainer.addMessageListener(scheduledEventResponceAdapter(), scheduledEventResponceTopic());
        redisContainer.addMessageListener(cancelEventResponceAdapter(), cancelEventResponceTopic());
        redisContainer.addMessageListener(forcedEventResponceAdapter(), forcedEventResponceTopic());

        return redisContainer;
    }

    @Bean
    public MessageListener authEventResponceAdapter() {
        return new MessageListenerAdapter(authEventResponseListener);
    }

    @Bean
    public MessageListener scheduledEventResponceAdapter() {
        return new MessageListenerAdapter(scheduledEventResponseListener);
    }

    @Bean
    public MessageListener cancelEventResponceAdapter() {
        return new MessageListenerAdapter(cancelEventResponseListener);
    }

    @Bean
    public MessageListener forcedEventResponceAdapter() {
        return new MessageListenerAdapter(forcedEventResponseListener);
    }

    @Bean
    public Topic authEventResponceTopic() {
        return new ChannelTopic(redisProperties.getAuthEventResponseTopic());
    }

    @Bean
    public Topic scheduledEventResponceTopic() {
        return new ChannelTopic(redisProperties.getScheduledEventResponseTopic());
    }

    @Bean
    public Topic cancelEventResponceTopic() {
        return new ChannelTopic(redisProperties.getCancelEventResponseTopic());
    }

    @Bean
    public Topic forcedEventResponceTopic() {
        return new ChannelTopic(redisProperties.getForcedEventResponseTopic());
    }

    @Bean
    public RedisTemplate<String, Object> customRedisTemplateObject() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        Jackson2JsonRedisSerializer<Object> jsonSerializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        redisTemplate.setValueSerializer(jsonSerializer);
        return redisTemplate;
    }
}