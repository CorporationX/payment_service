package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.pending.PendingDto;
import faang.school.paymentservice.dto.pending.PendingResponseDto;
import faang.school.paymentservice.service.PendingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pending")
public class PendingController {

    private final PendingService pendingService;

    @PostMapping
    public PendingResponseDto authorizationPending(@RequestBody @Valid PendingDto pendingDto){
        return pendingService.authorizationPending(pendingDto);
    }

    @PutMapping
    public PendingResponseDto forcedClearing(@RequestBody String operationId){
        return pendingService.forcedClearing(operationId);
    }
}
