package faang.school.paymentservice.publisher;

public interface MessagePublisher<T> {
    void publish(T message, String topic);
}
