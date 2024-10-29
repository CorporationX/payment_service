package faang.school.paymentservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.paymentservice.listener.PaymentApproveEventListener;
import faang.school.paymentservice.listener.PaymentCancelEventListener;
import faang.school.paymentservice.publisher.PaymentClearEventPublisher;
import faang.school.paymentservice.publisher.PaymentRequestEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.channels.paymentRequest}")
    private String paymentRequestChannel;

    @Value("${spring.data.redis.channels.paymentClear}")
    private String paymentClearChannel;

    @Value("${spring.data.redis.channels.paymentCancel}")
    private String paymentCancelChannel;

    @Value("${spring.data.redis.channels.paymentApprove}")
    private String paymentApproveChannel;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

    @Bean
    ChannelTopic paymentRequestTopic() {
        return new ChannelTopic(paymentRequestChannel);
    }

    @Bean
    ChannelTopic paymentClearTopic() {
        return new ChannelTopic(paymentClearChannel);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        template.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper()));
        return template;
    }

    @Bean
    public PaymentRequestEventPublisher paymentEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                                              ChannelTopic paymentRequestTopic) {
        return new PaymentRequestEventPublisher(redisTemplate, paymentRequestTopic);
    }

    @Bean
    public PaymentClearEventPublisher paymentClearEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                                                 ChannelTopic paymentClearTopic) {
        return new PaymentClearEventPublisher(redisTemplate, paymentClearTopic);
    }

    @Bean
    ChannelTopic paymentApproveTopic() {
        return new ChannelTopic(paymentApproveChannel);
    }

    @Bean
    ChannelTopic paymentCancelTopic() {
        return new ChannelTopic(paymentCancelChannel);
    }

    @Bean
    public MessageListenerAdapter paymentApprove(PaymentApproveEventListener listener) {
        return new MessageListenerAdapter(listener);
    }

    @Bean
    public MessageListenerAdapter paymentCancel(PaymentCancelEventListener listener) {
        return new MessageListenerAdapter(listener);
    }

    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(MessageListenerAdapter paymentApprove,
                                                                       MessageListenerAdapter paymentCancel,
                                                                       RedisConnectionFactory redisConnectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(redisConnectionFactory);
        container.addMessageListener(paymentApprove, paymentApproveTopic());
        container.addMessageListener(paymentCancel, paymentCancelTopic());
        return container;
    }
}
