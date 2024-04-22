package com.nhnacademy.aiot.ruleengine.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BatteryMessage {
    private Integer battery;
    private String device;
    private String place;
}