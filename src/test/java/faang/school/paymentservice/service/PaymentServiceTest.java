package faang.school.paymentservice.service;

import faang.school.paymentservice.client.AccountClient;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.account.AccountDto;
import faang.school.paymentservice.dto.payment.AuthorizationEvent;
import faang.school.paymentservice.dto.payment.AuthorizationMessage;
import faang.school.paymentservice.dto.payment.AuthorizationResponse;
import faang.school.paymentservice.exeption.GetAuthorizationBadRequest;
import faang.school.paymentservice.model.Request;
import faang.school.paymentservice.repository.PaymentRepository;
import faang.school.paymentservice.service.AOP.ErrorChecking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Import(ErrorChecking.class)
public class PaymentServiceTest {

    @MockBean
    private PaymentRepository paymentRepository;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @MockBean
    private AccountClient accountClient;

    @Autowired
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authorizePayment_successful() {
        AuthorizationMessage message = AuthorizationMessage.builder()
                .senderAccountId(1L)
                .recipientAccountId(2L)
                .currency(Currency.USD)
                .amount(BigDecimal.valueOf(2000))
                .senderNumber("1234567890")
                .recipientAccountNumber("0987654321")
                .build();

        AccountDto senderAccountDto = AccountDto.builder()
                .id(1L)
                .currency(Currency.USD)
                .build();

        when(accountClient.getAccount(1L)).thenReturn(senderAccountDto);

        when(paymentRepository.save(any(Request.class))).thenAnswer(invocation -> {
            Request request = invocation.getArgument(0);
            request.setId(1L);
            return request;
        });

        AuthorizationResponse response = paymentService.authorizePayment(message);

        assertNotNull(response);
        assertEquals(1L, response.getRequestId());
        assertNotNull(response.getVerificationCode());

        verify(paymentRepository, times(1)).save(any(Request.class));

        ArgumentCaptor<AuthorizationEvent> eventCaptor = ArgumentCaptor.forClass(AuthorizationEvent.class);
        verify(kafkaTemplate, times(1)).send(eq("authorization-topic"), eventCaptor.capture());

        AuthorizationEvent capturedEvent = eventCaptor.getValue();
        assertNotNull(capturedEvent);
        assertEquals(message.getRecipientAccountId(), capturedEvent.getRecipientAccountId());
        assertEquals(message.getSenderAccountId(), capturedEvent.getSenderAccountId());
        assertEquals(message.getAmount(), capturedEvent.getAmount());
        assertEquals(response.getVerificationCode(), capturedEvent.getVerificationCode());
    }

    @Test
    void authorizePayment_currencyMismatch() {
        AuthorizationMessage message = AuthorizationMessage.builder()
                .senderAccountId(1L)
                .recipientAccountId(2L)
                .currency(Currency.EUR)
                .amount(BigDecimal.valueOf(2000))
                .senderNumber("1234567890")
                .recipientAccountNumber("0987654321")
                .build();

        AccountDto senderAccountDto = AccountDto.builder()
                .id(1L)
                .currency(Currency.USD)
                .build();

        when(accountClient.getAccount(1L)).thenReturn(senderAccountDto);

        assertThrows(IllegalArgumentException.class, () -> paymentService.authorizePayment(message), "Currency does not match");
    }

    @Test
    void authorizePayment_sameSenderAndRecipient() {
        AuthorizationMessage message = AuthorizationMessage.builder()
                .senderAccountId(1L)
                .recipientAccountId(1L)
                .currency(Currency.USD)
                .amount(BigDecimal.valueOf(2000))
                .senderNumber("1234567890")
                .recipientAccountNumber("0987654321")
                .build();

        assertThrows(GetAuthorizationBadRequest.class, () -> paymentService.authorizePayment(message), "Sender and recipient cannot be the same");
    }
}