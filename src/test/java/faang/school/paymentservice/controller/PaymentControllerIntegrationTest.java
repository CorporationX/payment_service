package faang.school.paymentservice.controller;

import faang.school.paymentservice.client.account_service.AccountServiceClient;
import faang.school.paymentservice.dto.account.AccountDto;
import faang.school.paymentservice.dto.payment.PaymentRequestDto;
import faang.school.paymentservice.model.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import util.BaseContextTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static faang.school.paymentservice.dto.account.QueryType.NUMBER;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
public class PaymentControllerIntegrationTest extends BaseContextTest {
    @MockBean
    private AccountServiceClient accountServiceClient;

    @Test
    public void checkIdempotenceTest() throws Exception {

        PaymentRequestDto requestDto = PaymentRequestDto.builder()
                .amount("1000.00")
                .currency(Currency.RUB)
                .accountNumberFrom("00000000000000000000")
                .accountNumberTo("00000000000000000001")
                .clearScheduledAt(LocalDateTime.now().plusDays(1))
                .build();

        AccountDto accountFrom = AccountDto.builder()
                .id(UUID.randomUUID())
                .accountNumber("00000000000000000000")
                .externalId(1001L)
                .ownerType(AccountDto.OwnerType.USER)
                .accountType(AccountDto.AccountType.PERSONAL)
                .currency(Currency.RUB)
                .accountStatus(AccountDto.AccountStatus.ACTIVE)
                .build();

        AccountDto accountTo = AccountDto.builder()
                .id(UUID.randomUUID())
                .accountNumber("00000000000000000001")
                .externalId(1002L)
                .ownerType(AccountDto.OwnerType.PROJECT)
                .accountType(AccountDto.AccountType.CORPORATE)
                .currency(Currency.RUB)
                .accountStatus(AccountDto.AccountStatus.ACTIVE)
                .build();

        when(accountServiceClient.getAccountByNumber(NUMBER, requestDto.getAccountNumberFrom()))
                .thenReturn(List.of(accountFrom));
        when(accountServiceClient.getAccountByNumber(NUMBER, requestDto.getAccountNumberTo()))
                .thenReturn(List.of(accountTo));

        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(requestDto.getAmount()))
                .andExpect(jsonPath("$.currency").value("RUB"))
                .andExpect(jsonPath("$.status").value("AUTH_PENDING"));

        SECONDS.sleep(30);
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.message").value("This payment has already been processed"));

        SECONDS.sleep(40);
        mockMvc.perform(post("/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.amount").value(requestDto.getAmount()))
                .andExpect(jsonPath("$.currency").value("RUB"))
                .andExpect(jsonPath("$.status").value("AUTH_PENDING"));
    }
}

