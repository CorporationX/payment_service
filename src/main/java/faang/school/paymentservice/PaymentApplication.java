package faang.school.paymentservice;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableRetry
@EnableKafka
@EnableAsync
@ConfigurationPropertiesScan
public class PaymentApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(PaymentApplication.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
