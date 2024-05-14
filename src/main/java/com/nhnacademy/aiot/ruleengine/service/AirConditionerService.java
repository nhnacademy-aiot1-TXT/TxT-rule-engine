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
        if (!redisAdapter.hasKey(key + Constants.TIMER)) {
            redisAdapter.setValue(key + Constants.TIMER, payload.getTime());
        }
        return payload;
    }

    public boolean isIndoorTempMsg(Message<String> message) {
        String topic = message.getHeaders().get(Constants.MQTT_RECEIVED_TOPIC, String.class);
        if (topic == null) {
            return false;
        }
        return topic.contains(Constants.CLASS_A) && topic.contains(Constants.TEMPERATURE);
    }

    public boolean isTimerActive(String key, Payload payload) {
        return payload.getTime() - getTimer(key) <= Constants.ONE_MINUTE;
    }

    public Payload saveForAutoMode(MessageHeaders headers, Payload payload) {
        String[] topics = sensorService.getTopics(headers);

        String place = topics[6];
        String measurement = topics[10];

        redisAdapter.saveFloatToList(Constants.AIRCONDITIONER + ":" + place + ":" + measurement, payload.getValue());
        if (Constants.CLASS_A.equals(place) && Constants.TEMPERATURE.equals(measurement)) {
            redisAdapter.saveLongToList(Constants.AIRCONDITIONER + ":" + Constants.TIME + ":" + measurement, payload.getTime());
        }
        if (Constants.OUTDOOR.equals(place)) {
            redisAdapter.setValueToHash(Constants.PREVIOUS_OUTDOOR, measurement, payload.getValue());
        }

        return payload;
    }

    public Payload saveTemperature(Payload payload) {
        redisAdapter.saveFloatToList(Constants.TEMPERATURE, payload.getValue());
        return payload;
    }


    public Map<String, Object> getAvgForAutoMode() {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put(Constants.OUTDOOR_TEMPERATURE, getAvg(Constants.AIRCONDITIONER + ":" + Constants.OUTDOOR + ":" + Constants.TEMPERATURE));
            map.put(Constants.OUTDOOR_HUMIDITY, getAvg(Constants.AIRCONDITIONER + ":" + Constants.OUTDOOR + ":" + Constants.HUMIDITY));
        } catch (NoSuchElementException e) {
            map.put(Constants.OUTDOOR_TEMPERATURE, redisAdapter.getDoubleFromHash(Constants.PREVIOUS_OUTDOOR, Constants.TEMPERATURE));
            map.put(Constants.OUTDOOR_HUMIDITY, redisAdapter.getDoubleFromHash(Constants.PREVIOUS_OUTDOOR, Constants.HUMIDITY));
        }
        map.put(Constants.INDOOR_TEMPERATURE, getAvg(Constants.AIRCONDITIONER + ":" + Constants.CLASS_A + ":" + Constants.TEMPERATURE));
        map.put(Constants.INDOOR_HUMIDITY, getAvg(Constants.AIRCONDITIONER + ":" + Constants.CLASS_A + ":" + Constants.HUMIDITY));
        map.put(Constants.TOTAL_PEOPLE_COUNT, getAvg(Constants.AIRCONDITIONER + ":" + Constants.CLASS_A + ":" + Constants.TOTAL_PEOPLE_COUNT));
        map.put(Constants.TIME, redisAdapter.getLastLongValue(Constants.AIRCONDITIONER + ":" + Constants.TIME + ":" + Constants.TEMPERATURE));
        return map;
    }

    public void deleteForAutoMode() {
        redisAdapter.deleteListWithPrefix(Constants.AIRCONDITIONER + ":");
        redisAdapter.delete(Constants.AUTO_AIRCONDITIONER + Constants.TIMER);
    }

    public void deleteListAndTimer() {
        redisAdapter.delete(Constants.TEMPERATURE);
        redisAdapter.delete(Constants.AIRCONDITIONER + Constants.TIMER);
    }

    public Double getAvg(String key) {
        List<Double> list = redisAdapter.getAllDoubleList(key);
        return list.stream().mapToDouble(value -> value).average().orElseThrow();
    }

    private Long getTimer(String key) {
        return redisAdapter.getLongValue(key + Constants.TIMER);
    }
}
