package faang.school.paymentservice.exeption;

public class GetAuthorizationBadRequest extends RuntimeException {
    public GetAuthorizationBadRequest(String message) {
        super(message);
    }
}
