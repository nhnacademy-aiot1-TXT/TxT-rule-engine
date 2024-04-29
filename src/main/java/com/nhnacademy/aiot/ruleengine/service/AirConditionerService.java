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

@Service
@RequiredArgsConstructor
public class AirConditionerService {

    private final RedisAdapter redisAdapter;
    private final SensorService sensorService;

    public Payload setTimer(String key, Payload payload) {
        if (shouldSetTimer(key, payload)) {
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
        return payload.getTime() - getTimer(key) <= 60000;
    }

    public Payload saveForAutoMode(MessageHeaders headers, Payload payload) {
        String[] topics = sensorService.getTopics(headers);

        String place = topics[6];
        String measurement = topics[10];

        redisAdapter.saveFloatToList("airconditioner:" + place + ":" + measurement, payload.getValue());
        if ("class_a".equals(place) && "temperature".equals(measurement)) {
            redisAdapter.saveLongToList("airconditioner:time:" + measurement, payload.getTime());
        }

        return payload;
    }

    public Payload saveTemperature(Payload payload) {
        redisAdapter.saveFloatToList(Constants.TEMPERATURE, payload.getValue());
        return payload;
    }


    public Map<String, Float> getAvgForAutoMode() {
        Map<String, Float> map = new HashMap<>();
        map.put("outdoorTemperature", getAvg("airconditioner:outdoor:temperature"));
        map.put("outdoorHumidity", getAvg("airconditioner:outdoor:humidity"));
        map.put("indoorTemperature", getAvg("airconditioner:class_a:temperature"));
        map.put("indoorHumiditiy", getAvg("airconditioner:class_a:humidity"));
        map.put("totalPeopleCount", getAvg("airconditioner:class_a:total_people_count"));
        map.put("time", redisAdapter.getLastFloat("airconditioner:time:temperature"));
        return map;
    }

    public void deleteForAutoMode() {
        redisAdapter.deleteListWithPrefix("airconditioner:");
        redisAdapter.deleteListWithPrefix(Constants.AUTO_AIRCONDITIONER);
    }

    public void deleteListAndTimer() {
        redisAdapter.deleteListAndTimer(Constants.AIRCONDITIONER);
    }

    public Float getAvg(String key) {
        List<Float> list = redisAdapter.getAllFloatList(key);
        return (float) list.stream().mapToDouble(Float::doubleValue).average().orElseThrow();
    }

    private boolean shouldSetTimer(String key, Payload payload) {
        return !redisAdapter.hasTimer(key) || !isTimerActive(key, payload);
    }

    private Long getTimer(String key) {
        return redisAdapter.getTimer(key);
    }
}
