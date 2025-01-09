package faang.school.paymentservice.client;

import faang.school.paymentservice.context.UserContext;
import feign.RequestTemplate;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FeignUserInterceptor implements RequestInterceptor {
    private final UserContext userContext;

    @Override
    public void apply(RequestTemplate template) {
        template.header("x-user-id", String.valueOf(userContext.getUserId()));
    }
}
