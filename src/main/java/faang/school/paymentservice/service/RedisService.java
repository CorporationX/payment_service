package faang.school.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate<String, Double> redisTemplate;

    public void setValue(String key, Double value){
        redisTemplate.opsForValue().set(key, value);
    }

    public Double getValue(String key){
        if(Boolean.FALSE.equals(redisTemplate.hasKey(key))){
            throw new NullPointerException("Key = " + key + "is null");
        }
        return redisTemplate.opsForValue().get(key);
    }

    public void deleteValue(String key){
        if(Boolean.FALSE.equals(redisTemplate.hasKey(key))){
            throw new NullPointerException("Key = " + key + "is null");
        }
        redisTemplate.delete(key);
    }
}
