package faang.school.paymentservice.controller;


import faang.school.paymentservice.dto.CurrencyConverterDto;
import faang.school.paymentservice.dto.CurrencyRate;
import faang.school.paymentservice.service.currencyconverter.CurrencyConverterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CurrencyConverterController {
    private final CurrencyConverterService service;

    @GetMapping("/convert/currency")
    public CurrencyConverterDto convert(@RequestBody CurrencyConverterDto dto) {
        return service.convert(dto);
    }
}
