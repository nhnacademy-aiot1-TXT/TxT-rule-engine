package com.nhnacademy.aiot.ruleengine.domain;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SensorMeasurement {
    private Long time;
    private String device;
    private String place;
    private String topic;
    private String value;
    private String measurement;
}
