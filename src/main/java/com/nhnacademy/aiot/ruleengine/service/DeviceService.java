package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final RedisAdapter redisAdapter;

    public boolean isAirConditionerPowered() {
        return redisAdapter.getBooleanFromHash(Constants.DEVICE_POWER_STATUS, Constants.AIRCONDITIONER);
    }

    public boolean isAirCleanerPowered() {
        return redisAdapter.getBooleanFromHash(Constants.DEVICE_POWER_STATUS, Constants.AIRCLEANER);
    }

    public boolean isLightPowered() {
        return redisAdapter.getBooleanFromHash(Constants.DEVICE_POWER_STATUS, Constants.LIGHT);
    }

    public void setAirConditionerPower(boolean power) {
        redisAdapter.setValueToHash(Constants.DEVICE_POWER_STATUS, Constants.AIRCONDITIONER, power);
    }

    public void setAirCleanerPower(boolean power) {
        redisAdapter.setValueToHash(Constants.DEVICE_POWER_STATUS, Constants.AIRCLEANER, power);
    }

    public void setLightPower(boolean power) {
        redisAdapter.setValueToHash(Constants.DEVICE_POWER_STATUS, Constants.LIGHT, power);
    }

    public boolean isAutoMode() {
        return redisAdapter.getBooleanValue(Constants.AUTO_MODE);
    }
}
