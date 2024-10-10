package faang.school.paymentservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.Map;

@Data
@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "spring.cache.redis")
public class RedisCacheConfigurationProperties {
    @NotBlank
    private String host;

    @Min(1025)
    @Max(65536)
    private int port;

    @NotBlank
    private Map<String, String> caches;
}
