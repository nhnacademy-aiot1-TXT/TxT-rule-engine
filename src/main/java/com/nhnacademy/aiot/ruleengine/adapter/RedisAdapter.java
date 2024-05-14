package com.nhnacademy.aiot.ruleengine.adapter;

import com.nhnacademy.aiot.ruleengine.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RedisAdapter {

    private final SensorService sensorService;
    private final RedisTemplate<String, Object> redisTemplate;

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public void setValue(String key, Long value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public void setValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    public Long getLongValue(String key) {
        return (Long) redisTemplate.opsForValue().get(key);
    }

    public String getStringValue(String key) {
        return (String) redisTemplate.opsForValue().get(key);
    }

    public boolean getBooleanValue(String key) {
        return Boolean.parseBoolean((String) redisTemplate.opsForValue().get(key));
    }

    public void saveFloatToList(String key, String value) {
        redisTemplate.opsForList().rightPush(key, sensorService.parseToFloatValue(value));
    }

    public void saveStringToList(String key, String value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    public void saveLongToList(String key, Long value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    public List<Double> getAllDoubleList(String key) {
        return Objects.requireNonNull(redisTemplate.opsForList().range(key, 0, -1)).stream()
                      .mapToDouble(value -> Double.parseDouble(value.toString()))
                      .boxed()
                      .collect(Collectors.toList());
    }

    public List<String> getAllStringList(String key) {
        return Objects.requireNonNull(redisTemplate.opsForList().range(key, 0, -1)).stream()
                      .map(Object::toString)
                      .collect(Collectors.toList());
    }

    public Long getLastLongValue(String key) {
        return ((Double) Objects.requireNonNull(redisTemplate.opsForList().rightPop(key))).longValue();
    }

    public void delete(String key) {
        redisTemplate.delete(key);
    }

    public void deleteListWithPrefix(String prefix) {
        redisTemplate.delete(Objects.requireNonNull(redisTemplate.keys(prefix + "*")));
    }

    public boolean getBooleanFromHash(String key, String hashKey) {
        return Boolean.parseBoolean((String) redisTemplate.opsForHash().get(key, hashKey));
    }

    public Double getDoubleFromHash(String key, String hashKey) {
        return (Double) redisTemplate.opsForHash().get(key, hashKey);
    }

    public String getStringFromHash(String key, String hashKey) {
        return (String) redisTemplate.opsForHash().get(key, hashKey);
    }

    public void setValueToHash(String key, String hashKey, boolean power) {
        redisTemplate.opsForHash().put(key, hashKey, String.valueOf(power));
    }

    public void setValueToHash(String key, String hashKey, String value) {
        redisTemplate.opsForHash().put(key, hashKey, value);
    }
}
