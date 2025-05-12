package faang.school.paymentservice.properties.async;

public interface AsyncProperties {
    int poolSize();
    int shutdownTimeoutSeconds();
    String threadNamePrefix();
    boolean isWaitShutdown();
}
