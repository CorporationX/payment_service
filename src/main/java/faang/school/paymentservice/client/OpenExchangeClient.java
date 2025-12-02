package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.RatesResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "open-exchange",
            url = "${services.open_exchange.host}",
            path = "/api",
            configuration = RatesClientConfig.class)
public interface OpenExchangeClient {
    @GetMapping("/latest.json")
    RatesResponse getLatest(@RequestParam String base, @RequestParam String symbols);
}