package com.nhnacademy.aiot.ruleengine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.dto.sensor.BaseSensor;
import com.nhnacademy.aiot.ruleengine.dto.sensor.FloatValueSensor;
import com.nhnacademy.aiot.ruleengine.dto.sensor.StringValueSensor;
import com.nhnacademy.aiot.ruleengine.exception.PayloadParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SensorService {

    private final ObjectMapper objectMapper;

    public BaseSensor build(String topic, String payloadStr) {
        String[] topics = topic.split("/");
        Payload payload;
        try {
            payload = objectMapper.readValue(payloadStr, Payload.class);
        } catch (JsonProcessingException e) {
            throw new PayloadParseException();
        }

        BaseSensor.BaseSensorBuilder<?, ?> builder = isFloat(payload.getValue()) ?
                FloatValueSensor.builder().value(Math.round(Float.parseFloat(payload.getValue()) * 10) / 10f) :
                StringValueSensor.builder().value(payload.getValue());

        return builder
                .time(payload.getTime())
                .device(topics[8])
                .place(topics[6])
                .topic(String.join("/", topics))
                .measurement(topics[10])
                .build();
    }

    private boolean isFloat(String value) {
        try {
            Float.parseFloat(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
