package faang.school.paymentservice.client.service;

import faang.school.paymentservice.dto.payment.PaymentDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(name = "account-service", url = "${feign.client.account-service.host}:${feign.client.account-service.port}")
public interface AccountServiceClient {

    @PutMapping("api/v1/payments/auth/{paymentId}")
    PaymentDto authorizePayment(@PathVariable("paymentId") Long paymentId);

    @GetMapping("api/v1/payments/cancel/{paymentId}/user/{userId}")
    PaymentDto cancelPayment(@PathVariable("userId") Long userId, @PathVariable("paymentId") Long paymentId);

    @GetMapping("api/v1/payments/clear/{paymentId}")
    PaymentDto clearPayment(@PathVariable("paymentId") Long paymentId);
}