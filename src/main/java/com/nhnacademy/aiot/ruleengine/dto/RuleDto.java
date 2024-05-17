package com.nhnacademy.aiot.ruleengine.dto;

import java.time.LocalTime;
import java.util.List;
import java.util.Map;

public class RuleDto {
    String place;
    String deviceId;
    boolean occupancyCheckRequired;
    AiModeDto aiMode;
    CustomModeDto customMode;
}

class AiModeDto {
    List<MqttInDto> mqttInDtos;
    LocalTime timeInterval;
}

class ConditionSetDto {
    CompareConditionDto onCondition;
    CompareConditionDto offCondition;
}

class CompareConditionDto {
    ComparisonOperator comparisonOperator;
    float standardValue;
}

class CustomModeDto {
    Map<MqttInDto, ConditionSetDto> set;
    LocalTime timeInterval;
}

class MqttInDto {
    String url;
    String topic;
}

class ActionDto {
    Map<CompareConditionDto, Boolean> conditionResultMap;

}
