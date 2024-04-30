package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceService {
    private final RedisAdapter redisAdapter;

    public boolean isAirConditionerPowered() {
        return redisAdapter.isDevicePowered("airconditioner");
    }

    public boolean isAirCleanerPowered() {
        return redisAdapter.isDevicePowered("aircleaner");
    }

    public boolean isLightPowered() {
        return redisAdapter.isDevicePowered("light");
    }

    public boolean isAirConditionerAutoMode() {
        return redisAdapter.isDeviceAutoMode("airconditioner");
    }
}
