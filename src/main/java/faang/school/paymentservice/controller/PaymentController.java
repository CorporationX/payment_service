package faang.school.paymentservice.controller;

import faang.school.paymentservice.config.CurrencyExchangeConfig;
import faang.school.paymentservice.dto.*;
import faang.school.paymentservice.service.ConverterService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Random;

@Tag(name = "Payment API", description = "API для проведения платежей и операций с валютой")
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PaymentController {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");
    private static final String CONVERT_MONEY_MESS = "You convert %s %s to %s %s with commission %f%%";
    private static final String PAYMENT_MESSAGE = "payment on %s %s was accepted";

    private final CurrencyExchangeConfig exchangeConfig;
    private final ConverterService converterService;

    @Operation(
            summary = "Получить курс валют",
            description = "Возвращает текущий курс валют на основе данных от ConverterService"
    )
    @ApiResponse(responseCode = "200", description = "Успешное получение курса валют")
    @PostMapping("currency")
    public CurrencyExchangeResponse getCurrencyExchangeResponse() {
        return converterService.getCurrentCurrencyExchangeRate();
    }

    @Operation(
            summary = "Провести платеж",
            description = "Принимает платежные данные и возвращает результат платежа"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Платеж успешно обработан"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные")
    })
    @PostMapping("payment")
    public ResponseEntity<PaymentResponse> sendPayment(@RequestBody @Validated PaymentRequest dto) {
        String message = String.format(
                PAYMENT_MESSAGE,
                DECIMAL_FORMAT.format(dto.amount()),
                dto.currency()
        );

        return ResponseEntity.ok(new PaymentResponse(
                PaymentStatus.SUCCESS,
                getVerificationCode(),
                dto.paymentNumber(),
                dto.amount(),
                dto.currency(),
                message)
        );
    }

    @Operation(
            summary = "Обмен валюты",
            description = "Проводит обмен валюты с учетом комиссии"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Обмен валюты успешно выполнен"),
            @ApiResponse(responseCode = "400", description = "Некорректные входные данные")
    })
    @PostMapping("exchange")
    public ResponseEntity<PaymentResponse> exchangeCurrency(@RequestBody @Validated PaymentRequest dto,
                                                            @RequestParam Currency targetCurrency) {
        BigDecimal newAmount = converterService.convertWithCommission(dto, targetCurrency);

        String message = String.format(
                CONVERT_MONEY_MESS,
                DECIMAL_FORMAT.format(dto.amount()),
                dto.currency(),
                DECIMAL_FORMAT.format(newAmount),
                targetCurrency,
                exchangeConfig.commission()
        );

        return ResponseEntity.ok(new PaymentResponse(
                PaymentStatus.SUCCESS,
                getVerificationCode(),
                dto.paymentNumber(),
                newAmount,
                targetCurrency,
                message)
        );
    }

    private int getVerificationCode() {
        return new Random().nextInt(1000, 10000);
    }
}
