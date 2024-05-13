package com.nhnacademy.aiot.ruleengine.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeviceResponse {
    Long deviceId;
    String deviceName;
    LocalTime cycle;
}
