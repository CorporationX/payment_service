package faang.school.paymentservice.config.audit;

import faang.school.paymentservice.config.context.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<Long> {
    private final UserContext userContext;

    @Override
    public Optional<Long> getCurrentAuditor() {
        return Optional.of(userContext.getUserId());
    }
}
