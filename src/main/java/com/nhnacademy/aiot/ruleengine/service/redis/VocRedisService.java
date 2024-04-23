package com.nhnacademy.aiot.ruleengine.service.redis;

import com.nhnacademy.aiot.ruleengine.adapter.SensorRedisAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VocRedisService {

    public static final String VOC = "voc";
    public static final String AIR_CLEANER = "air_cleaner";

    private final SensorRedisAdapter sensorRedisAdapter;

    public boolean hasTimer() {
        return sensorRedisAdapter.hasTimer(AIR_CLEANER);
    }

    public void setTimer(Long time) {
        sensorRedisAdapter.setTimer(AIR_CLEANER, time);
    }

    public Long getTimer() {
        return sensorRedisAdapter.getTimer(AIR_CLEANER);
    }

    public void saveToList(String value) {
        sensorRedisAdapter.saveFloatToList(VOC, value);
    }

    public float getAvg() {
        List<Float> list = sensorRedisAdapter.getAllFloatList(VOC);
        return (float) list.stream().mapToDouble(Float::doubleValue).average().orElseThrow();
    }
}
