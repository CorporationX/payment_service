package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.BooleanResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("userService")
public interface UserServiceClient {

    @GetMapping("/v1/user/exist")
    BooleanResponse isUserExist(@RequestParam(name = "user_id") long userId);

    @PostMapping("/v1/premium/activate")
    void activatePremiumForUser(@RequestParam Long orderId);
}
