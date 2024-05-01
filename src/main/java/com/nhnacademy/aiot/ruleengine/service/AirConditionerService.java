package com.nhnacademy.aiot.ruleengine.service;

import com.nhnacademy.aiot.ruleengine.adapter.RedisAdapter;
import com.nhnacademy.aiot.ruleengine.constants.Constants;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class AirConditionerService {

    private final RedisAdapter redisAdapter;
    private final SensorService sensorService;

    public Payload setTimer(String key, Payload payload) {
        if (!redisAdapter.hasTimer(key)) {
            redisAdapter.setTimer(key, payload.getTime());
        }
        return payload;
    }

    public boolean isIndoorTempMsg(Message message) {
        String topic = message.getHeaders().get("mqtt_receivedTopic", String.class);
        if (topic == null) {
            return false;
        }
        return topic.contains("class_a") && topic.contains("temperature");
    }

    public boolean isTimerActive(String key, Payload payload) {
        System.out.println(payload.getTime() - getTimer(key));
        return payload.getTime() - getTimer(key) <= 600000;
    }

    public Payload saveForAutoMode(MessageHeaders headers, Payload payload) {
        String[] topics = sensorService.getTopics(headers);

        String place = topics[6];
        String measurement = topics[10];

        redisAdapter.saveFloatToList("airconditioner:" + place + ":" + measurement, payload.getValue());
        if ("class_a".equals(place) && "temperature".equals(measurement)) {
            redisAdapter.saveLongToList("airconditioner:time:" + measurement, payload.getTime());
        }
        if ("outdoor".equals(place)) {
            redisAdapter.saveHashes("previous_outdoor", measurement, payload.getValue());
        }

        return null;
    }

    public Payload saveTemperature(Payload payload) {
        redisAdapter.saveFloatToList(Constants.TEMPERATURE, payload.getValue());
        return payload;
    }


    public Map<String, Object> getAvgForAutoMode() {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("outdoorTemperature", getAvg("airconditioner:outdoor:temperature"));
            map.put("outdoorHumidity", getAvg("airconditioner:outdoor:humidity"));
        } catch (NoSuchElementException e) {
            map.put("outdoorTemperature", redisAdapter.getDoubleHashes("previous_outdoor", "temperature"));
            map.put("outdoorHumidity", redisAdapter.getDoubleHashes("previous_outdoor", "humidity"));
        }
        map.put("indoorTemperature", getAvg("airconditioner:class_a:temperature"));
        map.put("indoorHumidity", getAvg("airconditioner:class_a:humidity"));
        map.put("totalPeopleCount", getAvg("airconditioner:class_a:total_people_count"));
        map.put("time", redisAdapter.getLastLong("airconditioner:time:temperature"));
        return map;
    }

    public void deleteForAutoMode() {
        redisAdapter.deleteListWithPrefix("airconditioner:");
        redisAdapter.deleteTimer(Constants.AUTO_AIRCONDITIONER);
    }

    public void deleteListAndTimer() {
        redisAdapter.delete(Constants.TEMPERATURE);
        redisAdapter.deleteTimer(Constants.AIRCONDITIONER);
    }

    public Double getAvg(String key) {
        List<Double> list = redisAdapter.getAllDoubleList(key);
        return list.stream().mapToDouble(value -> value).average().getAsDouble();
    }

    private Long getTimer(String key) {
        return redisAdapter.getTimer(key);
    }
}
