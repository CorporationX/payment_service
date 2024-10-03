package faang.school.paymentservice.client.temp;

import faang.school.paymentservice.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class RetryRunner implements CommandLineRunner {
    @Autowired
    private WebClientRetryService webClientRetryService;

    @Autowired
    private CurrencyService currencyService;

    @Override
    public void run(String... args) throws Exception {
        Thread.sleep(10000);
        System.out.println("asdfjhasdfkh");
        currencyService.currencyRateFetcher();
    }

}
