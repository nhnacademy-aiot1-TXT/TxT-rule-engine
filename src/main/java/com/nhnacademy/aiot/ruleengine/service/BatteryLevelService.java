package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatteryLevelService {

    private final RedisAdapter redisAdapter;
    private static final int PLACE_INDEX = 6;
    private static final int DEVICE_INDEX = 8;
    private static final int TOPIC_LENGTH = 10;

    public Map<String, String> parseTopic(String topic) {
        String[] parts = topic.split("/");
        Map<String, String> results = new HashMap<>();

        if (parts.length >= TOPIC_LENGTH) {
            results.put("place", parts[PLACE_INDEX]);
            results.put("device", parts[DEVICE_INDEX]);
        }
        return results;
    }

    public void setBatteryStatus(String deviceId, String status) {
        redisAdapter.setBatteryStatus(deviceId, status);
    }

    public String getBatteryStatus(String deviceId) {
        return redisAdapter.getBatteryStatus(deviceId);
    }

}
