package faang.school.paymentservice;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
@EnableAutoConfiguration
public class PaymentApplicationApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(PaymentApplicationApp.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
