package faang.school.paymentservice.utils.idempotencyKey;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class IdempotencyKeyGenerator {

    public static String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            return Base64.getUrlEncoder().withoutPadding().encodeToString(hashBytes).substring(0, 64);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating idempotency key", e);
        }
    }
}
