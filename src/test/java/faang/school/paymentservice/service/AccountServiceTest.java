package faang.school.paymentservice.service;

import faang.school.paymentservice.client.ProjectServiceClient;
import faang.school.paymentservice.client.UserServiceClient;
import faang.school.paymentservice.dto.client.UserDto;
import faang.school.paymentservice.model.account.Account;
import faang.school.paymentservice.model.account.AccountStatus;
import faang.school.paymentservice.model.owner.Owner;
import faang.school.paymentservice.model.owner.OwnerType;
import faang.school.paymentservice.repository.AccountRepository;
import faang.school.paymentservice.repository.OwnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private OwnerRepository ownerRepository;

    @Mock
    private UserServiceClient userServiceClient;

    @InjectMocks
    private AccountService accountService;

    private Account account;
    private final String accountNumber = "00000000000000000000";

    @BeforeEach
    void setUp() {
        Owner owner = Owner.builder()
                .externalId(1L)
                .type(OwnerType.USER)
                .build();

        account = Account.builder()
                .owner(owner)
                .accountNumber(accountNumber)
                .build();
    }

    @Test
    void createAccount_shouldCreateNewOwner() {
        when(ownerRepository.findOwner(anyLong(), any())).thenReturn(Optional.empty());
        when(userServiceClient.getUser(anyLong())).thenReturn(UserDto.builder().id(1L).build());

        accountService.createAccount(account);

        verify(ownerRepository).save(any(Owner.class));
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void getAccountByNumber_shouldReturnAccount() {
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        accountService.getAccountByNumber(accountNumber);

        verify(accountRepository).findByAccountNumber(accountNumber);
    }

    @Test
    void freezeAccount_shouldFreezeAccount() {
        account.setStatus(AccountStatus.ACTIVE);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        accountService.freezeAccount(accountNumber);

        verify(accountRepository).findByAccountNumber(accountNumber);
    }

    @Test
    void freezeAccount_shouldThrowExceptionForNonActiveAccount() {
        account.setStatus(AccountStatus.FROZEN);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        assertThrows(IllegalStateException.class, () -> accountService.freezeAccount(accountNumber));
    }

    @Test
    void unfreezeAccount_shouldUnfreezeAccount() {
        account.setStatus(AccountStatus.FROZEN);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        accountService.unfreezeAccount(accountNumber);

        verify(accountRepository).findByAccountNumber(accountNumber);
    }

    @Test
    void closeAccount_shouldCloseAccount() {
        account.setStatus(AccountStatus.ACTIVE);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        accountService.closeAccount(accountNumber);

        verify(accountRepository).findByAccountNumber(accountNumber);
    }

    @Test
    void closeAccount_shouldThrowExceptionForAlreadyClosedAccount() {
        account.setStatus(AccountStatus.CLOSED);
        when(accountRepository.findByAccountNumber(accountNumber)).thenReturn(Optional.of(account));

        assertThrows(IllegalStateException.class, () -> accountService.closeAccount(accountNumber));
    }

    @Test
    void validateAccountNumber_shouldThrowExceptionForInvalidNumber() {
        String invalidAccountNumber = "00000000000aaa000000";

        assertThrows(IllegalArgumentException.class, () -> accountService.getAccountByNumber(invalidAccountNumber));
    }
}
