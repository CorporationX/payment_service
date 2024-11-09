package faang.school.paymentservice.utils.idempotencyKey;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class IdempotencyKeyGenerator {

    public static String hash(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            byte[] shortenedHash = new byte[48];
            System.arraycopy(hashBytes, 0, shortenedHash, 0, shortenedHash.length);

            return Base64.getUrlEncoder().withoutPadding().encodeToString(shortenedHash).substring(0, 64);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating idempotency key", e);
        }
    }
}
