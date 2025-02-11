package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.ExchangeResp;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "exchange-service", url = "${services.exchange-service.host}")
public interface PaymentServiceClient {

    @GetMapping("/api/latest.json")
    ExchangeResp getExchange(@RequestParam(name = "app_id") String appId);
}
