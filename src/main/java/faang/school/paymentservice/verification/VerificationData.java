package faang.school.paymentservice.verification;

import faang.school.paymentservice.dto.PaymentRequest;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.Random;
@Component
public class VerificationData {
    public int verificationCode(){
        return new Random().nextInt(1000, 10000);
    }

    public String addMessage(PaymentRequest dto){
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSum = decimalFormat.format(dto.amount());
        return String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %s %s was accepted.",
                formattedSum, dto.fromCurrency().name());
    }
}
