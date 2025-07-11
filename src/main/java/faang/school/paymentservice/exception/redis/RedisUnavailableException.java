package faang.school.paymentservice.exception.redis;

public class RedisUnavailableException extends RuntimeException {
    public RedisUnavailableException(String msg) {
        super(msg);
    }
}