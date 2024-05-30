package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final RedisAdapter redisAdapter;

    public boolean isDevicePowered(String place, String deviceName) {
        return redisAdapter.getBooleanFromHash(Constants.DEVICE_POWER_STATUS, place + "_" + deviceName);
    }

    public boolean isAiMode(String place, String deviceName) {
        return redisAdapter.getBooleanFromHash("ai_mode", place + "_" + deviceName);
    }

    public boolean isCustomMode(String place, String deviceName) {
        return redisAdapter.getBooleanFromHash("custom_mode", place + "_" + deviceName);
    }
}
