package com.nhnacademy.aiot.ruleengine.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Condition {
    private MqttInInfo message;
    private ComparisonOperator comparisonOperator;
    private float controlValue;
}
