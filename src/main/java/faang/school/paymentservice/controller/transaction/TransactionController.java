package faang.school.paymentservice.controller.transaction;

import faang.school.paymentservice.dto.transaction.TransactionDto;
import faang.school.paymentservice.dto.transaction.TransactionDtoToCreate;
import faang.school.paymentservice.service.transaction.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {
    private final TransactionService transactionService;

    @PostMapping
    public void createTransaction(@RequestBody @Valid TransactionDtoToCreate dto) {
        return transactionService.createTransaction(dto);
    }

    @GetMapping("/{transactionId}")
    public TransactionDto getTransaction(@PathVariable("transactionId") long id) {
        return transactionService.getTransaction(id);
    }

    @GetMapping("/clear/{transactionId}")
    public void clearTransaction(@PathVariable("transactionId") long id) {
        return transactionService.clearTransaction(id);
    }
}
