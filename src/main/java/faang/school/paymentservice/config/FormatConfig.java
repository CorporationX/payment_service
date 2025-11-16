package faang.school.paymentservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.DecimalFormat;

@Configuration
public class FormatConfig {

    @Bean
    public ThreadLocal<DecimalFormat> moneyFormatter() {
        return ThreadLocal.withInitial(() -> new DecimalFormat("0.00"));
    }
}
