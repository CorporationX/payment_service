package faang.school.paymentservice.config.topic.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.listener.ChannelTopic;

@Configuration
public class CancelPaymentTopic {

    @Bean
    public ChannelTopic cancelPaymentTopic(@Value("${spring.data.channel.premium_bought.name}") String topic) {
        return new ChannelTopic(topic);
    }
}
