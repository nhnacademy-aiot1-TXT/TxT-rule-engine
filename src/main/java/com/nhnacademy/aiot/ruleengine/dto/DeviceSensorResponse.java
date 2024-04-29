package com.nhnacademy.aiot.ruleengine.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceSensorResponse {
    private String sensorName;
    private Float onValue;
    private Float offValue;
}
