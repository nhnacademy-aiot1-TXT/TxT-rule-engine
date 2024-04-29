package com.nhnacademy.aiot.ruleengine.adapter;

import com.nhnacademy.aiot.ruleengine.exception.NosuchRedisListException;
import com.nhnacademy.aiot.ruleengine.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SensorRedisAdapter {

    public static final String TIMER = "_timer";
    public static final String STATUS = "_status";
    private final SensorService sensorService;
    private final RedisTemplate<String, Long> longRedisTemplate;
    private final RedisTemplate<String, Float> floatRedisTemplate;
    private final RedisTemplate<String, String> stringRedisTemplate;

    public boolean hasTimer(String key) {
        return Boolean.TRUE.equals(stringRedisTemplate.hasKey(key + TIMER));
    }

    public void setTimer(String key, Long time) {
        longRedisTemplate.opsForValue().set(key + TIMER, time);
    }

    public Long getTimer(String key) {
        return longRedisTemplate.opsForValue().get(key + TIMER);
    }

    public void saveFloatToList(String key, String value) {
        floatRedisTemplate.opsForList().rightPush(key, sensorService.parseToFloatValue(value));
    }

    public void saveStringToList(String key, String value) {
        stringRedisTemplate.opsForList().rightPush(key, value);
    }

    public String getStatus(String key) {
        return String.valueOf(stringRedisTemplate.opsForValue().get(key + STATUS));
    }

    public void setStatus(String key, String value) {
        stringRedisTemplate.opsForValue().set(key + STATUS, value);
    }

    public List<Float> getAllFloatList(String key) {
        List<Float> list = floatRedisTemplate.opsForList().range(key, 0, -1);
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

    public void deleteListAndTimer(String key) {
        stringRedisTemplate.delete(key);
        longRedisTemplate.delete(key + TIMER);
    }
}
