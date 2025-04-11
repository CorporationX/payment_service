package faang.school.paymentservice.service.payment;

import faang.school.paymentservice.dto.PaymentRequestDto;
import faang.school.paymentservice.dto.PaymentResponseDto;
import faang.school.paymentservice.dto.PaymentStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Random;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Override
    public ResponseEntity<PaymentResponseDto> sendPayment(PaymentRequestDto paymentRequestDto) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSum = decimalFormat.format(paymentRequestDto.amount());
        int verificationCode = new Random().nextInt(1000, 10000);
        String message = String.format("Dear friend! Thank you for your purchase! " +
                        "Your payment on %s %s was accepted.",
                formattedSum, paymentRequestDto.currency().name());

        return ResponseEntity.ok(new PaymentResponseDto(
                PaymentStatus.SUCCESS,
                verificationCode,
                paymentRequestDto.paymentNumber(),
                paymentRequestDto.amount(),
                paymentRequestDto.currency(),
                message)
        );
    }
}
