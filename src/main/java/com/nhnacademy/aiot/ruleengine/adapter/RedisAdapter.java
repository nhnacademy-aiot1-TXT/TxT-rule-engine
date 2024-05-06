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
    private final RedisTemplate<String, Double> doubleRedisTemplate;

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

    public List<Double> getAllDoubleList(String key) {
        List<Double> list = doubleRedisTemplate.opsForList().range(key, 0, -1);
        if (list != null) {
            return list;
        }
        throw new NosuchRedisListException();
    }

    public Long getLastLong(String key) {
        return longRedisTemplate.opsForList().rightPop(key);
    }

    public List<String> getAllStringList(String key) {
        List<String> list = stringRedisTemplate.opsForList().range(key, 0, -1);
        if (list != null) {
            return list;
        }
        throw new NosuchRedisListException();
    }

    public void delete(String key) {
        stringRedisTemplate.delete(key);
    }

    public void deleteTimer(String key) {
        longRedisTemplate.delete(key + Constants.TIMER);
    }

    public void deleteListWithPrefix(String prefix) {
        stringRedisTemplate.delete(Objects.requireNonNull(stringRedisTemplate.keys(prefix + "*")));
    }

    public boolean isDevicePowered(String deviceName) {
        return Boolean.parseBoolean((String) stringRedisTemplate.opsForHash().get("device_power_status", deviceName));
    }

    public void setDevicePower(String deviceName, boolean power) {
        stringRedisTemplate.opsForHash().put("device_power_status", deviceName, String.valueOf(power));
    }

    public boolean isDeviceAutoMode(String deviceName) {
        return Boolean.parseBoolean(stringRedisTemplate.opsForValue().get("auto_mode:" + deviceName));
    }

    public void saveHashes(String key, String hashkey, String value) {
        floatRedisTemplate.opsForHash().put(key, hashkey, value);
    }

    public Double getDoubleHashes(String key, String hashkey) {
        return (Double) floatRedisTemplate.opsForHash().get(key, hashkey);
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
