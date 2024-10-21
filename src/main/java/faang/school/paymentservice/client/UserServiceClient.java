package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.client.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user-service.host}:${user-service.port}/${user-service.path}")
public interface UserServiceClient {

    @GetMapping("/users/{user-id}")
    UserDto getUser(@PathVariable("user-id") long userId);
}