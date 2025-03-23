package faang.school;

import com.redis.testcontainers.RedisContainer;
import org.apache.kafka.clients.admin.Admin;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.util.List;
import java.util.Properties;


@Testcontainers
public abstract class BaseIntegrationTest {

    public static final DockerImageName KAFKA_IMAGE = DockerImageName.parse("confluentinc/cp-kafka:latest");
    public static final KafkaContainer KAFKA_CONTAINER = new KafkaContainer(KAFKA_IMAGE);
    public static final List<NewTopic> topicList = List.of(
            new NewTopic("auth-message-request-topic", 3, (short) 1),
            new NewTopic("auth-message-response-topic", 3, (short) 1),
            new NewTopic("cancel-message-request-topic", 3, (short) 1),
            new NewTopic("cancel-message-response-topic", 3, (short) 1),
            new NewTopic("clearing-message-request-topic", 3, (short) 1),
            new NewTopic("clearing-message-response-topic", 3, (short) 1),
            new NewTopic("auth-payment.DLT", 3, (short) 1)
    );
    private static final PostgreSQLContainer<?> POSTGRES_CONTAINER =
            new PostgreSQLContainer<>("postgres:13.3")
                    .withDatabaseName("postgres")
                    .withUsername("user")
                    .withPassword("password");
    private static final DockerImageName REDIS_IMAGE = DockerImageName.parse("redis:7.4.2");
    public static final RedisContainer REDIS_CONTAINER = new RedisContainer(REDIS_IMAGE);
    @LocalServerPort
    protected int port;
    protected WebTestClient webTestClient;

    @BeforeAll
    static void startContainer() {
        POSTGRES_CONTAINER.start();
        REDIS_CONTAINER.start();
        KAFKA_CONTAINER.start();
        createTopic();
    }

    private static void createTopic() {
        Properties properties = new Properties();
        properties.put(
                AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, KAFKA_CONTAINER.getBootstrapServers()
        );
        Admin admin = Admin.create(properties);
        admin.createTopics(topicList);
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.datasource.url", POSTGRES_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRES_CONTAINER::getPassword);

        registry.add("spring.data.redis.host", REDIS_CONTAINER::getHost);
        registry.add("spring.data.redis.port", REDIS_CONTAINER::getFirstMappedPort);

        registry.add("spring.kafka.bootstrap-servers", KAFKA_CONTAINER::getBootstrapServers);
        registry.add("spring.kafka.topics.payment-topics.clearing.response.topic-name",
                () -> "clearing-message-response-topic");
        registry.add("spring.kafka.topics.payment-topics.cancel.response.topic-name",
                () -> "cancel-message-response-topic");
        registry.add("spring.kafka.topics.payment-topics.authorization.response.topic-name",
                () -> "auth-message-response-topic");
        registry.add("spring.kafka.topics.payment-topics.clearing.request.topic-name",
                () -> "clearing-message-request-topic");
        registry.add("spring.kafka.topics.payment-topics.cancel.request.topic-name",
                () -> "cancel-message-request-topic");
        registry.add("spring.kafka.topics.payment-topics.authorization.request.topic-name",
                () -> "auth-message-request-topic");
        registry.add("spring.kafka.topics.payment-topics.response-listener-group", () -> "test-group");
    }

    @BeforeEach
    void setUpWebClient() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .defaultHeader("x-user-id", "1")
                .build();
    }
}
