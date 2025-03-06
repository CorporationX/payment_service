package faang.school.paymentservice.service;

import com.fasterxml.jackson.core.type.TypeReference;

public interface RedisService {
    <T> void save(String key, T value);

    <T> T get(String key, Class<T> clazz);

    <T> T get(String key, TypeReference<T> typeReference);

    void delete(String key);
}
