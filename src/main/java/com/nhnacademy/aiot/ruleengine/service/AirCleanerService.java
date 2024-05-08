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
        return redisAdapter.hasKey(Constants.AIRCLEANER + Constants.TIMER);
    }

    public Payload setTimer(Payload payload) {
        if (!hasTimer()) {
            redisAdapter.setValue(Constants.AIRCLEANER + Constants.TIMER, payload.getTime());
        }
        return payload;
    }

    private Long getTimer() {
        return redisAdapter.getLongValue(Constants.AIRCLEANER + Constants.TIMER);
    }

    public Payload saveVoc(Payload payload) {
        redisAdapter.saveFloatToList(Constants.VOC, payload.getValue());
        return payload;
    }

    public Double getAvg() {
        List<Double> list = redisAdapter.getAllDoubleList(Constants.VOC);
        return list.stream().mapToDouble(value -> value).average().getAsDouble();
    }

    public void deleteListAndTimer() {
        redisAdapter.delete(Constants.VOC);
        redisAdapter.delete(Constants.AIRCLEANER + Constants.TIMER);
    }

    public boolean isTimerActive(Payload payload) {
        return payload.getTime() - getTimer() <= 90000;
    }

}
