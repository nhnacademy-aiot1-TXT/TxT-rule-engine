package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class BatteryLevelService {
    public static final int LOW_LEVEL = 20;
    public static final int CRITICAL_LEVEL = 10;

    private final RedisAdapter redisAdapter;

    public void setBatteryStatus(String deviceId, String status) {
        redisAdapter.setBatteryStatus(deviceId, status);
    }

    public String getBatteryStatus(String deviceId) {
        return redisAdapter.getBatteryStatus(deviceId);
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
