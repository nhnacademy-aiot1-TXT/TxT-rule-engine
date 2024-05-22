package com.nhnacademy.aiot.ruleengine.dto.rule;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class AiModeDto {
    private List<MqttInDto> mqttInDtos;
    private LocalTime timeInterval;
}