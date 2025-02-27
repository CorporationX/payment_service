package faang.school.paymentservice.client;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Value;

public class OpenExchangeRatesInterceptor implements RequestInterceptor {
    @Value("${open_exchange_rates.app_id}")
    private String appId;

    @Override
    public void apply(RequestTemplate template) {
        template.query("app_id", appId);
    }
}
