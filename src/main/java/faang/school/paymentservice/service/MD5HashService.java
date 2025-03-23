package faang.school.paymentservice.service;

import faang.school.paymentservice.entity.Payment;
import jakarta.xml.bind.DatatypeConverter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Slf4j
@RequiredArgsConstructor
@Service
public class MD5HashService {

    public String getPaymentHash(Payment payment) {
        String paymentString = "%s, %s, %s, %s %s".formatted(
                payment.getSenderAccountNumber(),
                payment.getReceiverAccountNumber(),
                payment.getAmount().toString(),
                payment.getCurrency().name(),
                payment.getPaymentType().name()
        );
        return getMd5Hash(paymentString);
    }

    private String getMd5Hash(String input) {
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported in this platform");
        }
        byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));
        return DatatypeConverter.printHexBinary(messageDigest).toUpperCase();
    }

}
