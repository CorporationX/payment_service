package faang.school.paymentservice.handler;

@FunctionalInterface
public interface ErrorHandler {
    String handle(Exception ex);
}
