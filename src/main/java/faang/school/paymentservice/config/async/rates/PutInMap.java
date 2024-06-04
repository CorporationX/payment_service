package faang.school.paymentservice.config.async.rates;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class PutInMap {

    @Bean
    public Executor putRatesInMap(
            @Value("${async.put-in-map.threads-count}") int threadsCount
    ) {
        return Executors.newFixedThreadPool(threadsCount);
    }
}
