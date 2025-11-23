package faang.school.paymentservice.config.context;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class UserHeaderFilter implements Filter {
    private final UserContext userContext;
    @Value("${services.open-exchange.app-id}")
    private String DEFAULT_APP_ID;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String userAppId = request.getHeader("Authorization");
        if (userAppId != null) {
            userContext.setOpenAppId(userAppId);
        } else {
            userContext.setOpenAppId(DEFAULT_APP_ID);
        }
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            userContext.clear();
        }
    }
}
