package faang.school.paymentservice.service;

import faang.school.paymentservice.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {
    private static final String APP_ID = "0134aec31a134dec86124871d5fc0cbf";
    private static final String URL_SERVICE = "https://openexchangerates.org/api/latest.json?app_id=";

    public BigDecimal convertToRUB(PaymentRequest dto) {
        String urlStr = URL_SERVICE + APP_ID + "&base=" + dto.currency().name() + "&symbols=RUB";

        try {
            BigDecimal rub = getJsonObject(urlStr).getBigDecimal("RUB");

            BigDecimal percent = new BigDecimal("1.01");
            return dto.amount().multiply(rub).multiply(percent);
        } catch(IOException e){
            log.error(e.getMessage());
        }
        throw new NullPointerException("An unexpected error has occurred");
    }

    private static JSONObject getJsonObject(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();

        while ((inputLine = reader.readLine()) != null) {
            response.append(inputLine);
        }
        reader.close();

        JSONObject rates = new JSONObject(response.toString());

        return (JSONObject) rates.get("rates");
    }
}
