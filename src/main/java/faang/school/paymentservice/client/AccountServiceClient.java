package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.BalanceResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "account-service", url = "${account-service.host}:${account-service.port}")
public interface AccountServiceClient {
    @GetMapping("/balances/{accountId}")
    BalanceResponseDto findBalanceByAccountId(@PathVariable("accountId") UUID accountId);
}