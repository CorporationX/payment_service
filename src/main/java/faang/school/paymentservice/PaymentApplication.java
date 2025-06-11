package faang.school.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.cloud.openfeign.EnableFeignClients;
import faang.school.paymentservice.client.OpenexchangeConfig;

@EnableScheduling
@EnableRetry
@EnableConfigurationProperties
@SpringBootApplication
@EnableFeignClients(basePackages = "faang.school.paymentservice.client")
@EnableConfigurationProperties(OpenexchangeConfig.class)
public class PaymentApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }
}
