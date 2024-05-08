package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OccupancyService {

    private final RedisAdapter redisAdapter;

    public boolean hasTimer() {
        return redisAdapter.hasKey(Constants.OCCUPANCY + Constants.TIMER);
    }

    public Payload setTimer(Payload payload) {
        if (shouldStartTimer(payload)) {
            redisAdapter.setValue(Constants.OCCUPANCY + Constants.TIMER, payload.getTime());
        }
        return payload;
    }

    private boolean shouldStartTimer(Payload payload) {
        return !hasTimer() &&
                !payload.getValue().equals(getOccupancyStatus());
    }

    public Long getTimer() {
        return redisAdapter.getLongValue(Constants.OCCUPANCY + Constants.TIMER);
    }

    public String getOccupancyStatus() {
        return redisAdapter.getStringValue(Constants.OCCUPANCY + Constants.STATUS);
    }

    public void saveToList(String value) {
        redisAdapter.saveStringToList(Constants.OCCUPANCY, value);
    }

    public void setOccupancyStatus() {
        if (!getOccupancyStatus().equals(isOcucpied())) {
            redisAdapter.setValue(Constants.OCCUPANCY + Constants.STATUS, isOcucpied());
        }
        redisAdapter.delete(Constants.OCCUPANCY);
        redisAdapter.delete(Constants.OCCUPANCY + Constants.TIMER);
    }

    private String isOcucpied() {
        List<String> list = redisAdapter.getAllStringList(Constants.OCCUPANCY);
        return Collections.frequency(list, Constants.OCCUPIED) >= Collections.frequency(list, Constants.VACANT) ?
                Constants.OCCUPIED : Constants.VACANT;
    }

    public Payload updateOccupancy(Payload payload) {
        if (isTimerActive(payload)) {
            saveToList(payload.getValue());
        } else {
            setOccupancyStatus();
        }
        return payload;
    }

    private boolean isTimerActive(Payload payload) {
        return payload.getTime() - getTimer() <= 600000;
    }
}

