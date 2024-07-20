package com.nhnacademy.aiot.ruleengine.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceSensorResponse {
    private Long deviceId;
    private Long sensorId;
    private String sensorName;
    private Float onValue;
    private Float offValue;
}
