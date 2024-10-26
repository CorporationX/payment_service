package faang.school.paymentservice.publisher.redis;

public interface EventPublisher<T> {
    void publish(T event);
}