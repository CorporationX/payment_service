package faang.school.paymentservice.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;


    @Override
    public <T> void save(String key, T value) {

        log.info(String.format("Saving data to redis , key={%s}", key));

        try {
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue);
        } catch (JsonProcessingException e) {
            log.error("Error saving value to Redis", e);
            e.printStackTrace();
        }
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        log.info(String.format("Getting data from redis , key={%s}", key));
        String jsonValue = redisTemplate.opsForValue().get(key);
        try {
            return objectMapper.readValue(jsonValue, clazz);
        } catch (IOException e) {
            log.error("Error reading value from Redis", e);
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public <T> T get(String key, TypeReference<T> typeReference) {
        log.info(String.format("Getting data from redis , key={%s}", key));
        String jsonValue = redisTemplate.opsForValue().get(key);
        try {
            return objectMapper.readValue(jsonValue, typeReference);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error reading value from Redis", e);
        }
        return null;
    }

    @Override
    public void delete(String key) {
        log.info(String.format("Deleting data from redis , key={%s}", key));
        redisTemplate.delete(key);
    }
}