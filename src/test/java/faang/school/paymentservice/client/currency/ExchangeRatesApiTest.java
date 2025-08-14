package faang.school.paymentservice.client.currency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import faang.school.paymentservice.config.RetryConfig;
import faang.school.paymentservice.config.property.exchangerates.ExchangeRatesProperty;
import faang.school.paymentservice.config.property.exchangerates.RetryProperty;
import faang.school.paymentservice.config.property.exchangerates.UriProperty;
import faang.school.paymentservice.dto.CurrencyRateDto;
import faang.school.paymentservice.dto.Error;
import faang.school.paymentservice.exception.ApiRequestException;
import faang.school.paymentservice.exception.EmptyApiResponseException;
import faang.school.paymentservice.exception.ExternalApiException;
import faang.school.paymentservice.exception.InvalidApiResponseException;
import faang.school.paymentservice.exception.RetryExhaustedException;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@SuppressWarnings("checkstyle:VariableDeclarationUsageDistance")
class ExchangeRatesApiTest {
    private MockWebServer mockWebServer;
    private ExchangeRatesApi exchangeRatesApi;
    private ExchangeRatesProperty property;
    private Retry exchangeRatesRetry;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String BASE_URL = "/";
    private static final String KEY = "key";
    private static final String BASE_CURRENCY = "EUR";
    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int RESPONSE_TIMEOUT_MS = 5000;

    @BeforeAll
    static void init() {
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
    }

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        exchangeRatesApi = createApi();
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("Успешный GET /latest - 200 OK, success=true, error=null")
    void positive_shouldGetCurrencyRates() throws Exception {
        CurrencyRateDto expected = prepareCurrencyRateDto(true, false);

        mockWebServer.enqueue(new MockResponse()
                                      .setBody(toJson(expected))
                                      .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        StepVerifier.create(exchangeRatesApi.getCurrencyRates())
                .expectNext(expected)
                .verifyComplete();

        assertEquals(1, mockWebServer.getRequestCount());
        assertEquals("GET", mockWebServer.takeRequest().getMethod());
    }

    @Test
    @DisplayName("Успешный GET /latest с порторными запросами")
    void positive_shouldRetryAndGetCurrencyRates() throws Exception {
        CurrencyRateDto expected = prepareCurrencyRateDto(true, false);

        mockWebServer.enqueue(new MockResponse().setResponseCode(INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse()
                                      .setBody(toJson(expected))
                                      .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        StepVerifier.create(exchangeRatesApi.getCurrencyRates())
                .expectNext(expected)
                .verifyComplete();

        assertEquals(3, mockWebServer.getRequestCount());
        assertEquals("GET", mockWebServer.takeRequest().getMethod());
    }

    @Test
    @DisplayName("Ошибка GET /latest - 4xx - выброс ошибки и нет повтора вызова")
    void negative_whenHttpCode4xx_throwsErrorAndNotRetry() {
        String expectedMessage = String.format("API error %s at endpoint /latest", NOT_FOUND);
        mockWebServer.enqueue(new MockResponse().setResponseCode(NOT_FOUND.value()));

        StepVerifier.create(exchangeRatesApi.getCurrencyRates())
                .expectErrorMatches(error -> error instanceof ApiRequestException
                                             && error.getMessage().equals(expectedMessage))
                .verify();

        assertEquals(1, mockWebServer.getRequestCount());
    }

    @Test
    @DisplayName("Ошибка GET /latest - 5xx - выброс ошибки и повтор вызова")
    void negative_whenHttpCode5xx_throwsErrorAndRetry() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(INTERNAL_SERVER_ERROR.value()));
        mockWebServer.enqueue(new MockResponse().setResponseCode(INTERNAL_SERVER_ERROR.value()));

        StepVerifier.create(exchangeRatesApi.getCurrencyRates())
                .expectErrorMatches(error -> error instanceof RetryExhaustedException
                                             && error.getCause() != null
                                             && error.getCause() instanceof ExternalApiException)
                .verify();

        assertEquals(4, mockWebServer.getRequestCount());
    }

    @Test
    @DisplayName("Ошибка GET /latest - тело пустое")
    void negative_whenBodyIsEmpty_throwsError() {
        mockWebServer.enqueue(new MockResponse()
                                      .setResponseCode(200)
                                      .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                      .setBody(""));

        StepVerifier.create(exchangeRatesApi.getCurrencyRates())
                .verifyError(EmptyApiResponseException.class);

        assertEquals(1, mockWebServer.getRequestCount());
    }

    @Test
    @DisplayName("Ошибка GET /latest - 200 OK, success=false, error=not null")
    void negative_whenSuccessFalseAndErrorNotNull_throwsError() throws JsonProcessingException {
        CurrencyRateDto expected = prepareCurrencyRateDto(false, true);

        mockWebServer.enqueue(new MockResponse()
                                      .setBody(toJson(expected))
                                      .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        StepVerifier.create(exchangeRatesApi.getCurrencyRates())
                .verifyError(ApiRequestException.class);

        assertEquals(1, mockWebServer.getRequestCount());
    }

    @Test
    @DisplayName("Ошибка GET /latest - 200 OK, success=false, error=null")
    void negative_whenSuccessFalseAndErrorNull_throwsError() throws JsonProcessingException {
        CurrencyRateDto expected = prepareCurrencyRateDto(false, false);

        mockWebServer.enqueue(new MockResponse()
                                      .setBody(toJson(expected))
                                      .addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE));

        StepVerifier.create(exchangeRatesApi.getCurrencyRates())
                .verifyError(InvalidApiResponseException.class);

        assertEquals(1, mockWebServer.getRequestCount());
    }

    // ------------------------------

    private ExchangeRatesApi createApi() {
        property = createApiProperty();
        exchangeRatesRetry = new RetryConfig(createRetryProperty()).exchangeRatesRetry();
        return new ExchangeRatesApi(prepareWebClient(), exchangeRatesRetry, property);
    }

    private WebClient prepareWebClient() {
        String baseUrl = mockWebServer.url(BASE_URL).toString();
        return WebClient.builder().baseUrl(baseUrl).build();
    }

    private ExchangeRatesProperty createApiProperty() {
        UriProperty uriProperty = new UriProperty("/latest");
        RetryProperty retryProperty = createRetryProperty();
        return new ExchangeRatesProperty(KEY, BASE_URL, BASE_CURRENCY, "testCache", CONNECT_TIMEOUT_MS,
                                         RESPONSE_TIMEOUT_MS, uriProperty, retryProperty);
    }

    private RetryProperty createRetryProperty() {
        return new RetryProperty(3, 1000, ChronoUnit.MILLIS, 0.0);
    }

    private String toJson(Object object) throws JsonProcessingException {
        return OBJECT_MAPPER.writeValueAsString(object);
    }

    private CurrencyRateDto prepareCurrencyRateDto(boolean success, boolean isErrorExists) {
        Error error = null;
        if (isErrorExists) {
            error = new Error(103, "The requested API endpoint does not exist.");
        }

        return new CurrencyRateDto(
                success, 1519296206L, "EUR", LocalDate.now(), Map.of("EUR", 1.566015, "USD", 1.560132), error);
    }
}