package faang.school.paymentservice;

import org.springframework.boot.Banner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "faang.school.paymentservice.client")
public class PaymentApplicationApp {
    public static void main(String[] args) {
        new SpringApplicationBuilder(PaymentApplicationApp.class)
                .bannerMode(Banner.Mode.OFF)
                .run(args);
    }
}
