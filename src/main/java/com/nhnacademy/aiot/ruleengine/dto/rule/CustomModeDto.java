package com.nhnacademy.aiot.ruleengine.dto.rule;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.Map;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
class CustomModeDto {
    Map<MqttInDto, ConditionSetDto> mqttConditionMap;
    LocalTime timeInterval;
}
