package faang.school.paymentservice.config.context;

import org.springframework.stereotype.Component;

@Component
public class UserContext {
    ThreadLocal<String> userAppIdHolder = new ThreadLocal<>();

    public String getOpenAppId() {
        return userAppIdHolder.get();
    }

    public void setOpenAppId(String userAppId) {
        userAppIdHolder.set(userAppId);
    }

    public void clear() {
        userAppIdHolder.remove();
    }
}
