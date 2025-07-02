package faang.school.paymentservice.service.account;

import faang.school.paymentservice.config.TestContainersConfig;
import faang.school.paymentservice.entity.account.FreeAccountNumber;
import faang.school.paymentservice.model.account.AccountType;
import faang.school.paymentservice.repository.account.AccountNumbersSequenceRepository;
import faang.school.paymentservice.repository.account.FreeAccountNumbersRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@SpringBootTest
public class FreeAccountNumbersServiceIT extends TestContainersConfig {

    @Autowired
    private FreeAccountNumbersService accountNumbersService;

    @Autowired
    private FreeAccountNumbersRepository accountNumbersRepository;

    @MockBean
    private AccountNumbersSequenceRepository spyRepository;

    @Test
    void shouldReturnAndRemoveFreeAccountNumber() {
        String expectedNumber = "4276000000000011";
        accountNumbersRepository.save(new FreeAccountNumber(expectedNumber, AccountType.DEBIT));

        String usedNumber = accountNumbersService.useAccountNumber(AccountType.DEBIT, Function.identity());

        assertThat(usedNumber).isEqualTo(expectedNumber);
        assertThat(accountNumbersRepository.findById(expectedNumber)).isEmpty();
    }

    @Test
    void shouldRetryOnOptimisticLockFailureAndEventuallySucceed() {
        AccountType accountType = AccountType.DEBIT;
        long initialNumber = 123L;
        String expectedAccountNumber = accountType.getPrefix() + String.format("%012d", initialNumber + 1);

        Mockito.doReturn(Optional.of(initialNumber))
                .when(spyRepository).getCurrentNumber(accountType);

        AtomicInteger counter = new AtomicInteger();
        Mockito.doAnswer(inv -> counter.incrementAndGet() >= 3)
                .when(spyRepository).incrementIfEquals(accountType, initialNumber);

        String result = accountNumbersService.useAccountNumber(accountType, Function.identity());

        assertThat(result).isEqualTo(expectedAccountNumber);
        Mockito.verify(spyRepository, Mockito.times(3)).incrementIfEquals(accountType, initialNumber);
    }

    @Test
    void shouldFailWhenNoSequenceExists() {
        assertThrows(IllegalArgumentException.class, () ->
                accountNumbersService.useAccountNumber(AccountType.DEBIT, Function.identity())
        );
    }
}
