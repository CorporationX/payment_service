package faang.school.paymentservice.client;

import faang.school.paymentservice.model.dto.AccountDto;
import feign.FeignException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "account-service", url = "${account-service.host}:${account-service.port}")
public interface AccountServiceClient {

    @GetMapping("/api/v1/account/number/{number}")
    @Retryable(
            value = {FeignException.class},
            maxAttempts = 5,
            backoff = @Backoff(delay = 5000)
    )
    ResponseEntity<AccountDto> getAccountNumber(@PathVariable String number);
}
