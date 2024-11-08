package faang.school.paymentservice.controller;


import faang.school.paymentservice.dto.PendingDto;
import faang.school.paymentservice.service.PendingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payment")
@RequiredArgsConstructor
public class PendingController {

    private final PendingService pendingService;

    @PostMapping("/pending")
    public Long initAndSaving(@RequestBody PendingDto pendingDto, @RequestHeader("X-Pending-Data") UUID uuid) {
        return pendingService.initAndSaving(pendingDto, uuid);
    }

    @PutMapping("/cancel/{id}")
    public PendingDto cancelPending(@PathVariable Long id, @RequestHeader("X-Pending-Data") UUID uuid) {
        return pendingService.cancelPending(id, uuid);
    }

    @PutMapping("/forced/{id}")
    public PendingDto forcedPaymentConfirmation(@PathVariable Long id, @RequestHeader("X-Pending-Data") UUID uuid) {
        return pendingService.forcedPaymentConfirmation(id, uuid);
    }

    @GetMapping("/get/{id}")
    public PendingDto getPending(@PathVariable Long id) {
        return pendingService.getPending(id);
    }
}
