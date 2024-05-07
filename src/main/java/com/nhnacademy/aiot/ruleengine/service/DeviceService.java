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

    public void setAirConditionerPower(boolean power) {
        redisAdapter.setDevicePower("airconditioner", power);
    }

    public void setAirCleanerPower(boolean power) {
        redisAdapter.setDevicePower("aircleaner", power);
    }

    public void setLightPower(boolean power) {
        redisAdapter.setDevicePower("light", power);
    }

    public boolean isAirConditionerAutoMode() {
        return redisAdapter.isDeviceAutoMode("airconditioner");
    }
}
