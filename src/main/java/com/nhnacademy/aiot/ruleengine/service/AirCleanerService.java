package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AirCleanerService {

    private final RedisAdapter redisAdapter;

    private boolean hasTimer() {
        return redisAdapter.hasTimer(Constants.AIRCLEANER);
    }

    public Payload setTimer(Payload payload) {
        if (!hasTimer()) {
            redisAdapter.setTimer(Constants.AIRCLEANER, payload.getTime());
        }
        return payload;
    }

    private Long getTimer() {
        return redisAdapter.getTimer(Constants.AIRCLEANER);
    }

    public Payload saveVoc(Payload payload) {
        redisAdapter.saveFloatToList(Constants.VOC, payload.getValue());
        return payload;
    }

    public float getAvg() {
        List<Float> list = redisAdapter.getAllFloatList(Constants.VOC);
        return (float) list.stream().mapToDouble(Float::doubleValue).average().orElseThrow();
    }

    public void deleteListAndTimer() {
        redisAdapter.deleteListAndTimer(Constants.VOC);
    }

    public boolean isTimerActive(Payload payload) {
        return payload.getTime() - getTimer() <= 60000;
    }

}