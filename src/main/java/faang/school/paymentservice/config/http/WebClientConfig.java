package faang.school.paymentservice.config.http;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient
                .builder()
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .filter(this::errorHandler);
    }

    private Mono<ClientResponse> errorHandler(ClientRequest request, ExchangeFunction next) {
        return next.exchange(request)
                .flatMap(res -> {
                    if (res.statusCode().isError()) {
                        return Mono.error(new RuntimeException("Error: API call failed."));
                    } else {
                        return Mono.just(res);
                    }
                });
    }
}
