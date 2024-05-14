package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.CommonAdapter;
import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.TimeZone;

@Service
@RequiredArgsConstructor
public class OccupancyService {

    private final RedisAdapter redisAdapter;
    private final CommonAdapter commonAdapter;

    public boolean shouldStartProcess(Payload payload, String deviceName) {
        return !payload.getValue()
                       .equals(redisAdapter.getStringFromHash(Constants.OCCUPANCY + Constants.STATUS, deviceName));
    }

    public Payload setTimer(Payload payload, String deviceName) {
        if (!redisAdapter.hasKey(deviceName + Constants.OCCUPANCY_LABEL + Constants.TIMER)) {
            redisAdapter.setValue(deviceName + Constants.OCCUPANCY_LABEL + Constants.TIMER, payload.getTime());
        }
        return payload;
    }

    public Long getTimer(String deviceName) {
        return redisAdapter.getLongValue(deviceName + Constants.OCCUPANCY_LABEL + Constants.TIMER);
    }

    public boolean isTimerActive(Payload payload, String deviceName) {
        if (Constants.VACANT.equals(getOccupancyStatus(deviceName))) {
            return payload.getTime() - getTimer(deviceName) <= Constants.TEN_MINUTES;
        }
        return getLocalTime(payload.getTime() - getTimer(deviceName))
                .isBefore(commonAdapter.getDeviceByName(deviceName).getCycle());
    }

    public Payload save(Payload payload, String deviceName) {
        redisAdapter.saveStringToList(deviceName + Constants.OCCUPANCY_LABEL, payload.getValue());
        return payload;
    }

    public Payload updateOccupancy(Payload payload, String deviceName) {
        redisAdapter.setValueToHash(Constants.OCCUPANCY + Constants.STATUS, deviceName, isOccupied(deviceName + Constants.OCCUPANCY_LABEL));
        redisAdapter.delete(deviceName + Constants.OCCUPANCY_LABEL);
        redisAdapter.delete(deviceName + Constants.OCCUPANCY_LABEL + Constants.TIMER);
        return payload;
    }

    public String getOccupancyStatus(String deviceName) {
        return redisAdapter.getStringFromHash(Constants.OCCUPANCY + Constants.STATUS, deviceName);
    }

    private String isOccupied(String key) {
        List<String> list = redisAdapter.getAllStringList(key);
        return Collections.frequency(list, Constants.OCCUPIED) >= Collections.frequency(list, Constants.VACANT) ?
                Constants.OCCUPIED : Constants.VACANT;
    }

    private LocalTime getLocalTime(Long time) {
        return LocalTime.ofInstant(Instant.ofEpochMilli(time), TimeZone.getDefault().toZoneId());
    }
}

