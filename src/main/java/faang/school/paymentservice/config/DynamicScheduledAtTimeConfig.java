package faang.school.paymentservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class DynamicScheduledAtTimeConfig {
    @Value("#{T(java.time.LocalDateTime).now().plusMinutes(3)}")
    private LocalDateTime scheduledAt;

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }
}