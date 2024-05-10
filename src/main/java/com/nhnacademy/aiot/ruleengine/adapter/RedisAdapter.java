package com.nhnacademy.aiot.ruleengine.adapter;

import com.nhnacademy.aiot.ruleengine.exception.NosuchRedisListException;
import com.nhnacademy.aiot.ruleengine.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class RedisAdapter {

    private final SensorService sensorService;
    private final RedisTemplate<String, Long> longRedisTemplate;
    private final RedisTemplate<String, Float> floatRedisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;
    private final RedisTemplate<String, Double> doubleRedisTemplate;

    public boolean hasKey(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key));
    }

    public void setValue(String key, Long time) {
        longRedisTemplate.opsForValue().set(key, time);
    }

    public Long getLongValue(String key) {
        return longRedisTemplate.opsForValue().get(key);
    }

    public boolean getBooleanValue(String key) {
        return Boolean.parseBoolean(stringRedisTemplate.opsForValue().get(key));
    }

    public void saveFloatToList(String key, String value) {
        floatRedisTemplate.opsForList().rightPush(key, sensorService.parseToFloatValue(value));
    }

    public void saveStringToList(String key, String value) {
        stringRedisTemplate.opsForList().rightPush(key, value);
    }

    public void saveLongToList(String key, Long value) {
        longRedisTemplate.opsForList().rightPush(key, value);
    }

    public List<Double> getAllDoubleList(String key) {
        List<Double> list = doubleRedisTemplate.opsForList().range(key, 0, -1);
        if (list != null) {
            return list;
        }
        throw new NosuchRedisListException();
    }

    public List<String> getAllStringList(String key) {
        List<String> list = stringRedisTemplate.opsForList().range(key, 0, -1);
        if (list != null) {
            return list;
        }
        throw new NosuchRedisListException();
    }

    public Long getLastLongValue(String key) {
        return longRedisTemplate.opsForList().rightPop(key);
    }

    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    public void deleteListWithPrefix(String prefix) {
        stringRedisTemplate.delete(Objects.requireNonNull(stringRedisTemplate.keys(prefix + "*")));
    }

    public boolean getBooleanFromHash(String key, String hashKey) {
        return Boolean.parseBoolean((String) stringRedisTemplate.opsForHash().get(key, hashKey));
    }

    public Double getDoubleFromHash(String key, String hashKey) {
        return (Double) floatRedisTemplate.opsForHash().get(key, hashKey);
    }

    public String getStringFromHash(String key, String hashKey) {
        return (String) stringRedisTemplate.opsForHash().get(key, hashKey);
    }

    public void setValueToHash(String key, String hashKey, boolean power) {
        stringRedisTemplate.opsForHash().put(key, hashKey, String.valueOf(power));
    }

    public void setHashes(String key, String hashKey, String value) {
        floatRedisTemplate.opsForHash().put(key, hashKey, value);
    }

    // 디바이스의 배터리 상태를 설정하는 메소드
    public void setBatteryStatus(String deviceId, String status) {
        stringRedisTemplate.opsForValue().set("battery_status:" + deviceId, status);
    }

    // 디바이스의 현재 배터리 상태를 가져오는 메소드
    public String getBatteryStatus(String deviceId) {
        return stringRedisTemplate.opsForValue().get("battery_status:" + deviceId);
    }
}
