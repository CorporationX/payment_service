package faang.school.paymentservice.client;

import faang.school.paymentservice.config.FeignClientConfiguration;
import faang.school.paymentservice.dto.ExchangeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "exchange-service",
        url = "${services.exchange-service.url}",
        configuration = FeignClientConfiguration.class)
public interface ExchangeServiceClient {

    @GetMapping("/api/latest.json")
    ExchangeResponse exchange(@RequestHeader("Authorization") String token);
}