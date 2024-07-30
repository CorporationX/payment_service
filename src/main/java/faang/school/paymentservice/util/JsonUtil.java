package faang.school.paymentservice.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Evgenii Malkov
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class JsonUtil {

    private final ObjectMapper objectMapper;

    @Nullable
    public String parseObjectAsString(Object payload) {
        try {
            return this.objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            log.error("Failed parse object", e);
            return null;
        }
    }

    public <T> T mapToObject(String data, Class<T> clazz) throws JsonProcessingException {
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(formatter));
        javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(formatter));
        objectMapper.registerModule(javaTimeModule);

        return objectMapper.readValue(data, clazz);
    }
}
