package faang.school.paymentservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
@EnableRetry
public class PaymentApplication {
    public static void main(String[] args) {

        SpringApplication.run(PaymentApplication.class, args);
    }
}
