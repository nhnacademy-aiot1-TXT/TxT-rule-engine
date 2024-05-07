package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatteryLevelService {

    private final RedisAdapter redisAdapter;

    public void setBatteryStatus(String deviceId, String status) {
        redisAdapter.setBatteryStatus(deviceId, status);
    }

    public String getBatteryStatus(String deviceId) {
        return redisAdapter.getBatteryStatus(deviceId);
    }

}
