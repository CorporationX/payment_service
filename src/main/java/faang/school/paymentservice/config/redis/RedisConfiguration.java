package faang.school.paymentservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import faang.school.paymentservice.listener.payment.PaymentEventListener;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@RequiredArgsConstructor
public class RedisConfiguration {

    private final RedisProperties redisProperties;
    private final PaymentEventListener paymentEventListener;
    private final ObjectMapper objectMapper;

    @Bean
    public ChannelTopic paymentAuthPendingTopic() {
        return new ChannelTopic(redisProperties.getChannels().getPaymentAuthPendingChannel().getName());
    }

    @Bean
    public ChannelTopic paymentAuthErrorTopic() {
        return new ChannelTopic(redisProperties.getChannels().getPaymentAuthErrorChannel().getName());
    }

    @Bean
    public ChannelTopic paymentAuthSuccessTopic() {
        return new ChannelTopic(redisProperties.getChannels().getPaymentAuthSuccessChannel().getName());
    }

    @Bean
    public ChannelTopic paymentConfirmPendingTopic() {
        return new ChannelTopic(redisProperties.getChannels().getPaymentConfirmPendingChannel().getName());
    }

    @Bean
    public ChannelTopic paymentConfirmErrorTopic() {
        return new ChannelTopic(redisProperties.getChannels().getPaymentConfirmErrorChannel().getName());
    }

    @Bean
    public ChannelTopic paymentConfirmSuccessTopic() {
        return new ChannelTopic(redisProperties.getChannels().getPaymentConfirmSuccessChannel().getName());
    }

    @Bean
    public ChannelTopic paymentCancelPendingTopic() {
        return new ChannelTopic(redisProperties.getChannels().getPaymentCancelPendingChannel().getName());
    }

    @Bean
    public ChannelTopic paymentCancelErrorTopic() {
        return new ChannelTopic(redisProperties.getChannels().getPaymentCancelErrorChannel().getName());
    }

    @Bean
    public ChannelTopic paymentCancelSuccessTopic() {
        return new ChannelTopic(redisProperties.getChannels().getPaymentCancelSuccessChannel().getName());
    }

    @Bean
    public ChannelTopic paymentClearPendingTopic() {
        return new ChannelTopic(redisProperties.getChannels().getPaymentClearPendingChannel().getName());
    }

    @Bean
    public ChannelTopic paymentClearErrorTopic() {
        return new ChannelTopic(redisProperties.getChannels().getPaymentClearErrorChannel().getName());
    }

    @Bean
    public ChannelTopic paymentClearSuccessTopic() {
        return new ChannelTopic(redisProperties.getChannels().getPaymentClearSuccessChannel().getName());
    }

    @Bean
    public MessageListener paymentEventMessageListener() {
        return new MessageListenerAdapter(paymentEventListener);
    }

    @Bean
    public RedisMessageListenerContainer redisContainer(JedisConnectionFactory jedisConnectionFactory) {
        RedisMessageListenerContainer redisContainer = new RedisMessageListenerContainer();
        redisContainer.setConnectionFactory(jedisConnectionFactory);

        redisContainer.addMessageListener(paymentEventMessageListener(), paymentAuthSuccessTopic());
        redisContainer.addMessageListener(paymentEventMessageListener(), paymentAuthErrorTopic());
        redisContainer.addMessageListener(paymentEventMessageListener(), paymentConfirmSuccessTopic());
        redisContainer.addMessageListener(paymentEventMessageListener(), paymentConfirmErrorTopic());
        redisContainer.addMessageListener(paymentEventMessageListener(), paymentCancelSuccessTopic());
        redisContainer.addMessageListener(paymentEventMessageListener(), paymentCancelErrorTopic());
        redisContainer.addMessageListener(paymentEventMessageListener(), paymentClearSuccessTopic());
        redisContainer.addMessageListener(paymentEventMessageListener(), paymentClearErrorTopic());

        return redisContainer;
    }

    @Bean
    public JedisConnectionFactory jedisConnectionFactory() {
        return new JedisConnectionFactory();
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(JedisConnectionFactory jedisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper));

        return redisTemplate;
    }
}

