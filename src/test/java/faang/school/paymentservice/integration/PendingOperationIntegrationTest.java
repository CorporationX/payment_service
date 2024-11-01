package faang.school.paymentservice.integration;

import faang.school.paymentservice.dto.CheckingAccountBalance;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.OperationMessage;
import faang.school.paymentservice.model.AccountBalanceStatus;
import faang.school.paymentservice.model.Category;
import faang.school.paymentservice.model.OperationStatus;
import faang.school.paymentservice.model.PendingOperation;
import faang.school.paymentservice.publisher.EventPublisher;
import faang.school.paymentservice.repository.PendingOperationRepository;
import faang.school.paymentservice.service.PendingOperationService;
import faang.school.paymentservice.test_config.TestContainersConfig;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static faang.school.paymentservice.test_config.TestContainersConfig.redisContainer;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
@ActiveProfiles("test")
@ContextConfiguration(classes = TestContainersConfig.class)
public class PendingOperationIntegrationTest {
    @Autowired
    private PendingOperationService pendingOperationService;

    @Autowired
    private PendingOperationRepository pendingOperationRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private EventPublisher<OperationMessage> eventPublisher;

    @BeforeAll
    public static void startRedisContainer() {
        redisContainer.start();
        System.setProperty("spring.redis.host", redisContainer.getHost());
        System.setProperty("spring.redis.port", redisContainer.getMappedPort(6379).toString());
    }

    @Test
    public void testInitiateOperation_SavesEntityAndPublishesMessage() {
        PendingOperation operation = PendingOperation.builder()
                .sourceAccountId(UUID.randomUUID())
                .targetAccountId(UUID.randomUUID())
                .idempotencyKey("unique-key-123")
                .amount(new BigDecimal("100.00"))
                .currency(Currency.USD)
                .category(Category.CHARITY)
                .clearScheduledAt(LocalDateTime.now().plusDays(1))
                .status(OperationStatus.PENDING)
                .accountBalanceStatus(AccountBalanceStatus.BALANCE_NOT_VERIFIED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        UUID operationId = pendingOperationService.initiateOperation(operation);

        PendingOperation savedOperation = pendingOperationRepository.findById(operationId).orElse(null);
        assertNotNull(savedOperation);
        assertEquals(operation.getIdempotencyKey(), savedOperation.getIdempotencyKey());

        ArgumentCaptor<OperationMessage> messageCaptor = ArgumentCaptor.forClass(OperationMessage.class);
        verify(eventPublisher, times(1)).publish(messageCaptor.capture());

        OperationMessage publishedMessage = messageCaptor.getValue();
        assertNotNull(publishedMessage);
        assertEquals(operationId, publishedMessage.getOperationId());
        assertEquals(operation.getAmount(), publishedMessage.getAmount());
        assertEquals(operation.getCurrency(), publishedMessage.getCurrency());
        assertEquals(operation.getCategory(), publishedMessage.getCategory());
        assertEquals(operation.getStatus(), publishedMessage.getStatus());

        CheckingAccountBalance balanceEvent = CheckingAccountBalance.builder()
                .operationId(operationId)
                .sourceAccountId(operation.getSourceAccountId())
                .status(AccountBalanceStatus.SUFFICIENT_FUNDS)
                .build();

        redisTemplate.convertAndSend("checking_balance", balanceEvent);

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            PendingOperation updatedOperation = pendingOperationRepository.findById(operationId).orElse(null);
            assertNotNull(updatedOperation);
            assertEquals(AccountBalanceStatus.SUFFICIENT_FUNDS, updatedOperation.getAccountBalanceStatus());
            assertEquals(OperationStatus.AUTHORIZATION, updatedOperation.getStatus());
        });
    }
}