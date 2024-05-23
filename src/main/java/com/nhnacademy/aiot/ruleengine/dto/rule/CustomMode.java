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
public class CustomMode {
    private boolean occupancyCheckRequired;
    private Map<MqttInInfo, ConditionSet> mqttConditionMap;
    private LocalTime timeInterval;
}
