package com.nhnacademy.aiot.ruleengine.dto.rule;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;
import java.util.Map;

@Getter
@AllArgsConstructor
public class CustomModeDto {
    private Map<MqttInDto, ConditionSetDto> mqttConditionMap;
    private LocalTime timeInterval;
}
