package faang.school.paymentservice.config.redis;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "spring.data.redis")
public class RedisProperties {

    private Channels channels;

    @Getter
    @Setter
    protected static class Channels {
        private Channel paymentAuthPendingChannel;
        private Channel paymentAuthErrorChannel;
        private Channel paymentAuthSuccessChannel;

        private Channel paymentConfirmPendingChannel;
        private Channel paymentConfirmErrorChannel;
        private Channel paymentConfirmSuccessChannel;

        private Channel paymentCancelPendingChannel;
        private Channel paymentCancelErrorChannel;
        private Channel paymentCancelSuccessChannel;

        private Channel paymentClearPendingChannel;
        private Channel paymentClearErrorChannel;
        private Channel paymentClearSuccessChannel;
    }

    @Getter
    @Setter
    protected static class Channel {
        private String name;
    }
}
