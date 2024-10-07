package faang.school.paymentservice.client;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import faang.school.paymentservice.model.dto.ExchangeRatesDto;
import faang.school.paymentservice.service.ExchangeRates;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

@SpringBootTest
@ActiveProfiles("test")
public class ExchangeRatesTests {

    private WireMockServer wireMockServer;

    @Autowired
    private ExchangeRates exchangeRates;

    @Value("${services.exchangerates.appId}")
    private String appId;

    @BeforeEach
    public void setup() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();
        WireMock.configureFor("localhost", 8081);
    }

    @AfterEach
    public void teardown() {
        wireMockServer.stop();
    }

    @Test
    public void testWebClient() {
        wireMockServer.stubFor(WireMock.get(urlPathEqualTo("/latest"))
                .withQueryParam("access_key", equalTo(appId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"success\":\"true\",\"timestamp\":5793002,\"base\":\"EUR\",\"date\":\"2024-09-18\",\"rates\":{\"RUB\":5.344,\"USD\":94.34}}")));

        Mono<ExchangeRatesDto> result = exchangeRates.fetchData();

        StepVerifier.create(result)
                .expectNextMatches(dto -> dto != null && dto.getRates() != null && dto.getBase().equals("EUR")
                        && dto.getRates().get("RUB").equals("5.344"))
                .verifyComplete();

        wireMockServer.verify(WireMock.getRequestedFor(urlPathEqualTo("/latest")).withQueryParam("access_key", equalTo(appId)));
    }

    @Test
    public void testWebClientReturnNullDto() {
        wireMockServer.stubFor(WireMock.get(urlPathEqualTo("/latest"))
                .withQueryParam("access_key", equalTo(appId))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"success\":\"\",\"timestamp\":,\"base\":\"\",\"date\":\"\",\"rates\":{}}")));

        Mono<ExchangeRatesDto> result = exchangeRates.fetchData();

        StepVerifier.create(result)
                .expectComplete()
                .verify();

        wireMockServer.verify(WireMock.getRequestedFor(urlPathEqualTo("/latest")).withQueryParam("access_key", equalTo(appId)));
    }
}