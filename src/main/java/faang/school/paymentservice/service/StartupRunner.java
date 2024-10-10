package faang.school.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class StartupRunner implements CommandLineRunner {

    private final CurrencyService currencyService;

    @Override
    public void run(String... args) {
        currencyService.currencyRateFetcher();
    }
}
