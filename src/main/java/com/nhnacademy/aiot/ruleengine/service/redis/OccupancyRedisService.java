package com.nhnacademy.aiot.ruleengine.service.redis;

import com.nhnacademy.aiot.ruleengine.adapter.SensorRedisAdapter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OccupancyRedisService {

    public static final String VACANT = "vacant";
    public static final String OCCUPIED = "occupied";
    public static final String OCCUPANCY = "occupancy";

    private final SensorRedisAdapter sensorRedisAdapter;

    public boolean hasTimer() {
        return sensorRedisAdapter.hasTimer(OCCUPANCY);
    }

    public void setTimer(Long time) {
        sensorRedisAdapter.setTimer(OCCUPANCY, time);
    }

    public Long getTimer() {
        return sensorRedisAdapter.getTimer(OCCUPANCY);
    }

    public String getOccupancyStatus() {
        return sensorRedisAdapter.getStatus(OCCUPANCY);
    }


    public void saveToList(String value) {
        sensorRedisAdapter.saveStringToList(OCCUPANCY, value);
    }

    public void setOccupancyStatus() {
        sensorRedisAdapter.setStatus(OCCUPANCY,isOcucpied());
        sensorRedisAdapter.deleteListAndTimer(OCCUPANCY);
    }

    private String isOcucpied() {
        List<String> list = sensorRedisAdapter.getAllStringList(OCCUPANCY);
        return Collections.frequency(list, OCCUPIED) >= Collections.frequency(list, VACANT) ?
                OCCUPIED : VACANT;
    }
}
