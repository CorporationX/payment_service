package faang.school.paymentservice.config.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.paymentservice.publicher.PaymentClearEventPublisher;
import faang.school.paymentservice.publicher.PaymentRequestEventPublisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.channels.paymentRequest}")
    private String paymentRequestChannel;

    @Value("${spring.data.redis.channels.paymentClear}")
    private String paymentClearChannel;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }

   @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
       RedisTemplate<String, Object> template = new RedisTemplate<>();
       template.setConnectionFactory(factory);
       template.setKeySerializer(new GenericJackson2JsonRedisSerializer());
       return template;
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
    public PaymentRequestEventPublisher paymentEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                                                             ChannelTopic paymentRequestTopic) {
       return new PaymentRequestEventPublisher(redisTemplate, paymentRequestTopic);
   }

   @Bean
    public PaymentClearEventPublisher paymentClearEventPublisher(RedisTemplate<String, Object> redisTemplate,
                                                                 ChannelTopic paymentClearTopic) {
        return new PaymentClearEventPublisher(redisTemplate, paymentClearTopic);
   }
}
