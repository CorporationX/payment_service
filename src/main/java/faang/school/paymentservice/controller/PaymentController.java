package faang.school.paymentservice.controller;

import faang.school.paymentservice.dto.PaymentRequest;
import faang.school.paymentservice.dto.PaymentResponse;
import faang.school.paymentservice.dto.PaymentStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.DecimalFormat;
import java.util.Random;

/**
 * Контроллер для обработки запросов на проведение платежей.
 * <p>
 * Эмулирует поведение платёжного сервиса, возвращая успешный результат при получении валидного запроса.
 * Используется для межсервисного взаимодействия с другими микросервисами, такими как user_service.
 * </p>
 * <p>
 * Эндпоинт:
 * <ul>
 *   <li><b>POST /api/payment</b> — принимает запрос на платёж и возвращает результат платежа.</li>
 * </ul>
 * <p>
 * Заголовки:
 * <ul>
 *   <li><b>x-user-id</b> — идентификатор пользователя, совершающего платёж.</li>
 * </ul>
 * <p>
 * Тело запроса: {@link PaymentRequest}
 * Ответ: {@link PaymentResponse}
 *
 * @author agent
 * @since 10.07.2025
 */
@RestController
@RequestMapping("/api")
public class PaymentController {

    @PostMapping("/payment")
    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto,
                                                       @RequestHeader("x-user-id") Long userId) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        String formattedSum = decimalFormat.format(dto.amount());
        int verificationCode = new Random().nextInt(1000, 10000);
        String message = String.format("Dear friend! Thank you for your purchase! " +
                                       "Your payment on %s %s was accepted.",
                formattedSum, dto.currency().name());

        return ResponseEntity.ok(new PaymentResponse(
                PaymentStatus.SUCCESS,
                verificationCode,
                dto.paymentNumber(),
                dto.amount(),
                dto.currency(),
                message)
        );
    }
}
