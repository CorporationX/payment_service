package faang.school.paymentservice.config.currencyrate;

import faang.school.paymentservice.PaymentApplication;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Scanner;

@Component
public class CurrencyRateParams {
    private final String accessKeyPath;

    @Getter
    private String accessKey;

    public CurrencyRateParams (@Value("${currency-rate-fetcher.access_key_path}") String accessKeyPath) {
        this.accessKeyPath = accessKeyPath;
        setAccessKey();
    }

    private void setAccessKey(){
        if (accessKeyPath == null) {
            throw new NullPointerException("пустой путь до ключа");
        }
        URL resource = PaymentApplication.class.getResource(accessKeyPath);
        assert resource != null;
        try {
            Scanner scanner = new Scanner(new File(resource.getPath()));
            String line = scanner.nextLine();
            this.accessKey = line.trim();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
