package com.nhnacademy.aiot.ruleengine.adapter;

import com.nhnacademy.aiot.ruleengine.constants.Constants;
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

    public boolean hasTimer(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key + Constants.TIMER));
    }

    public void setTimer(String key, Long time) {
        longRedisTemplate.opsForValue().set(key + Constants.TIMER, time);
    }

    public Long getTimer(String key) {
        return longRedisTemplate.opsForValue().get(key + Constants.TIMER);
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

    public String getStatus(String key) {
        return String.valueOf(stringRedisTemplate.opsForValue().get(key + Constants.STATUS));
    }

    public void setStatus(String key, String value) {
        stringRedisTemplate.opsForValue().set(key + Constants.STATUS, value);
    }

    public List<Float> getAllFloatList(String key) {
        List<Float> list = floatRedisTemplate.opsForList().range(key, 0, -1);
        if (list != null) {
            return list;
        }
        throw new NosuchRedisListException();
    }

    public Float getLastFloat(String key) {
        return floatRedisTemplate.opsForList().rightPop(key);
    }

    public List<String> getAllStringList(String key) {
        List<String> list = stringRedisTemplate.opsForList().range(key, 0, -1);
        if (list != null) {
            return list;
        }
        throw new NosuchRedisListException();
    }

    public void deleteListAndTimer(String key) {
        stringRedisTemplate.delete(key);
        longRedisTemplate.delete(key + Constants.TIMER);
    }

    public void deleteListWithPrefix(String prefix) {
        stringRedisTemplate.delete(Objects.requireNonNull(stringRedisTemplate.keys(prefix)));
    }

    public boolean isDevicePowered(String deviceName) {
        return Boolean.parseBoolean((String) stringRedisTemplate.opsForHash().get("device_power_status", deviceName));
    }

    public boolean isDeviceAutoMode(String deviceName) {
        return Boolean.parseBoolean(stringRedisTemplate.opsForValue().get("auto_mode:" + deviceName));
    }
}
