package faang.school.paymentservice.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Payment Service API",
                version = "1.0",
                description = "Документация API сервиса платежей",
                contact = @Contact(
                        name = "FAANG School",
                        url = "https://faang.school",
                        email = "customer.care@faang.school"
                )
        )
)
public class SwaggerConfig {
}
