package faang.school.paymentservice.message.publisher;

public interface MessagePublisher<T> {
    void publish(T message);
}