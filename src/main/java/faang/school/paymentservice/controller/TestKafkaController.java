package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.TestKafkaDto;
import faang.school.paymentservice.service.TestKafkaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestKafkaController {

    private final TestKafkaService testKafkaService;

    @PostMapping("/kafka/send")
    public String send(@RequestBody TestKafkaDto dto) {
        return testKafkaService.send(dto.getTopic(), dto.getMessage());
    }
}
