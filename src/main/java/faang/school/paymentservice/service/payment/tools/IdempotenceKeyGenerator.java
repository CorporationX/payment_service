package faang.school.paymentservice.service.payment.tools;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

@Component
public class IdempotenceKeyGenerator {

    @Value("${payment.idempotency.algorithm}")
    private String algorithm;

    public String generateIdempotenceKey(String... data) {
        try {
            StringBuilder keyData = new StringBuilder();
            for (String dataItem : data) {
                keyData.append(dataItem);
            }
            MessageDigest digest = MessageDigest.getInstance(algorithm);
            byte[] hash = digest.digest(keyData.toString().getBytes());
            return convertToString(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private String convertToString(byte[] hash) {
        try (Formatter formatter = new Formatter()) {
            for (byte b : hash) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        }
    }
}
