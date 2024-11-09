package faang.school.paymentservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.redis.testcontainers.RedisContainer;
import faang.school.paymentservice.dto.Currency;
import faang.school.paymentservice.dto.event.dmsevent.DmsEventDto;
import faang.school.paymentservice.dto.event.dmsevent.DmsTypeOperation;
import faang.school.paymentservice.dto.request.RequestDto;
import faang.school.paymentservice.entity.request.Request;
import faang.school.paymentservice.entity.request.RequestStatus;
import faang.school.paymentservice.mapper.RequestMapper;
import faang.school.paymentservice.repository.RequestRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@Testcontainers
@Sql("/payment_request_controller/create_table_request.sql")
public class PaymentRequestControllerIT {
    @Autowired
    private PaymentRequestController controller;

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private RequestMapper requestMapper;

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;
    private final LocalDateTime now = LocalDateTime.now();
    private RequestDto requestDto;
    private DmsEventDto expectedDmsEventDto;
    private String requestBody;

    @Container
    protected static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER =
        new PostgreSQLContainer<>("postgres:latest")
            .withReuse(true);

    @Container
    protected static final RedisContainer REDIS_CONTAINER =
        new RedisContainer(DockerImageName.parse("redis/redis-stack:latest"))
            .withReuse(true);

    @DynamicPropertySource
    protected static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);

        registry.add("spring.data.redis.port", () -> REDIS_CONTAINER.getMappedPort(6379));
        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    protected static void beforeEach() {
        POSTGRESQL_CONTAINER.start();
        REDIS_CONTAINER.start();
    }

    @BeforeEach
    public void setup() throws JsonProcessingException {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper = objectMapper;
        beforeEach();
        requestDto = new RequestDto(
            null,
            1L,
            2L,
            BigDecimal.valueOf(10.00).setScale(2, RoundingMode.HALF_UP),
            Currency.USD,
            LocalDateTime.of(2024, 11, 5, 20, 30, 0),
            now,
            now
        );
        expectedDmsEventDto = new DmsEventDto (
            null,
            1L,
            2L,
            BigDecimal.valueOf(10.00).setScale(2, RoundingMode.HALF_UP),
            Currency.USD,
            DmsTypeOperation.AUTHORIZATION,
            LocalDateTime.of(2024, 11, 5, 20, 30, 0)
        );
        requestBody = objectMapper.writeValueAsString(requestDto);
    }

    @Test
    public void testAuthorizePayment_Response() throws Exception {

        // Act & Assert
        performPostAuthorization();
    }

    @Test
    public void testAuthorizePayment_CheckSaveRequestInDb() throws Exception {
        // Arrange
        Request expectedRequest = requestMapper.toRequest(requestDto);

        // Act
        performPostAuthorization();

        // Assert
        List<Request> requests = requestRepository.findAll();
        Request request = requests.get(0);
        assertNotNull(request.getId());
        assertEquals(expectedRequest.getSenderId(), request.getSenderId());
        assertEquals(expectedRequest.getReceiverId(), request.getReceiverId());
        assertEquals(expectedRequest.getAmount(), request.getAmount());
        assertEquals(expectedRequest.getCurrency(), request.getCurrency());
        assertEquals(expectedRequest.getClearScheduledAt(), request.getClearScheduledAt());
        assertEquals(RequestStatus.PENDING, request.getStatus());

    }

    @Test
    public void testAuthorizePayment_CheckEventInRedis() {
        try (Jedis jedis = new Jedis(REDIS_CONTAINER.getHost(), REDIS_CONTAINER.getMappedPort(6379))) {
            DmsEventPubSub pubSub = new DmsEventPubSub();

            Thread subscriberThread = new Thread(() -> jedis.subscribe(pubSub, "dms_channel"));
            subscriberThread.start();

            performPostAuthorization();
            List<Request> requests = requestRepository.findAll();
            Request request = requests.get(0);
            assertEquals(1, requests.size());
            assertNotNull(request.getId());
            expectedDmsEventDto.setRequestId(request.getId());
            String receivedMessage = pubSub.getReceivedMessage();
            DmsEventDto dmsEventDto = objectMapper.readValue(receivedMessage, DmsEventDto.class);
            assertEquals(expectedDmsEventDto, dmsEventDto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public void performPostAuthorization() {
        try {
            mockMvc.perform(MockMvcRequestBuilders.post("/api/payment/authorization")
                    .contentType(MediaType.APPLICATION_JSON).content(requestBody))
                .andExpect(status().isOk());
        } catch (Exception e) {
            throw new RuntimeException(e);

        }
    }
    private static class DmsEventPubSub extends JedisPubSub {
        private String receivedMessage;

        public void onMessage(String channel, String message) {
            receivedMessage = message;
        }

        public String getReceivedMessage() {
            return receivedMessage;
        }
    }
}
