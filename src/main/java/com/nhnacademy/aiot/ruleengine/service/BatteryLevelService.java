package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import static com.nhnacademy.aiot.ruleengine.constants.Constants.CRITICAL_LEVEL;
import static com.nhnacademy.aiot.ruleengine.constants.Constants.LOW_LEVEL;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatteryLevelService {
    private final RedisAdapter redisAdapter;

    public void setBatteryStatus(String deviceId, String status) {
        redisAdapter.setValue("battery_status:" + deviceId, status);
    }

    public String getBatteryStatus(String deviceId) {
        return redisAdapter.getStringValue("battery_status:" + deviceId);
    }

    public boolean isLowLevel(Payload payload) {
        int level = Integer.parseInt(payload.getValue());
        return level <= LOW_LEVEL && level > CRITICAL_LEVEL;
    }

    public boolean isCriticalLevel(Payload payload) {
        int level = Integer.parseInt(payload.getValue());
        return level <= CRITICAL_LEVEL;
    }

    public boolean alreadyReportCriticalStatus(String deviceId) {
        return Constants.CRITICAL.equals(getBatteryStatus(deviceId));
    }

    public boolean alreadyReportLowStatus(String deviceId) {
        return Constants.LOW.equals(getBatteryStatus(deviceId));
    }
}
