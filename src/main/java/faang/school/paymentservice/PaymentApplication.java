package faang.school.paymentservice;

import faang.school.paymentservice.config.ExchangeRatesProperties;
import faang.school.paymentservice.config.RedisCacheConfigurationProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableRetry
@EnableConfigurationProperties({RedisCacheConfigurationProperties.class, ExchangeRatesProperties.class})
public class PaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }
}
