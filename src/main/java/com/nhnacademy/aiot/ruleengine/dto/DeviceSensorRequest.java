package com.nhnacademy.aiot.ruleengine.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceSensorRequest {
    private String deviceName;
    private String sensorName;
    private String placeName;
    private Float onValue;
    private Float offValue;
}
