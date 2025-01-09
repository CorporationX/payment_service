package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.PaymentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(name = "Account-service", url = "${services.account-service.host}:${services.account-service.port}")
public interface AccountServiceClient {
    @PutMapping("/api/v1/balances/{accountNumber}")
    PaymentStatus reserveMoney(@PathVariable String accountNumber, @RequestParam BigDecimal amount);
}
