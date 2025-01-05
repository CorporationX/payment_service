package faang.school.paymentservice.client;

import faang.school.paymentservice.dto.PendingDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;


@Component
@RequiredArgsConstructor
public class WebClientForAccountService {

    private final WebClient webClient;

    public void sendRequestCancelPending(PendingDto pendingDto, String url) {
        try {
            webClient.put()
                    .uri(url)
                    .bodyValue(pendingDto)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new RuntimeException("Error canceling payment: " + e.getMessage(), e);
        }
    }
}
