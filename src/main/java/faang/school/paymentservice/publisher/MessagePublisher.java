package faang.school.paymentservice.publisher;

import faang.school.paymentservice.event.Event;

public interface MessagePublisher<T extends Event> {
    void publish(T event);
}
