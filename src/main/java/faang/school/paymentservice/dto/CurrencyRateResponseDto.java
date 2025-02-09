package faang.school.paymentservice.dto;

import java.util.Map;

public record CurrencyRateResponseDto(

        /** True or false depending on whether or not your API request has succeeded. */
        boolean success,

        /** Exact date and time (UNIX time stamp) the given rates were collected. */
        String timestamp,

        /** Three-letter currency code of the base currency used for request. */
        String base,

        /** Date of request */
        String date,

        /** Exchange rate data for the currencies you have requested. */
        Map<String, Double> rates
) {
}
