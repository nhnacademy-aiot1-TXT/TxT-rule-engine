package com.nhnacademy.aiot.ruleengine.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.aiot.ruleengine.dto.Payload;
import com.nhnacademy.aiot.ruleengine.dto.SensorData;
import com.nhnacademy.aiot.ruleengine.exception.PayloadParseException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SensorService {

    private final ObjectMapper objectMapper;

    public SensorData build(String topic, String payloadStr) {
        String[] topics = topic.split("/");
        Payload payload = convertStringToPayload(payloadStr);

        return SensorData.builder()
                         .time(payload.getTime())
                         .device(topics[8])
                         .place(topics[6])
                         .topic(String.join("/", topics))
                         .measurement(topics[10])
                         .value(payload.getValue())
                         .build();
    }

    public Payload convertStringToPayload(String payload) {
        try {
            return objectMapper.readValue(payload, Payload.class);
        } catch (JsonProcessingException e) {
            throw new PayloadParseException();
        }
    }

    public Float parseToFloatValue(String value) {
        return Math.round(Float.parseFloat(value) * 10) / 10f;
    }
}
