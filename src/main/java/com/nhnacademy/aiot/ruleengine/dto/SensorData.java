package com.nhnacademy.aiot.ruleengine.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SensorData {
    private Long time;
    private String place;
    private String topic;
    private String device;
    private String measurement;
    private String value;
}
