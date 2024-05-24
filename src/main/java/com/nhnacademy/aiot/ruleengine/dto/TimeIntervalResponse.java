package com.nhnacademy.aiot.ruleengine.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TimeIntervalResponse {
    private Long timeIntervalId;
    private Long sensorId;
    private String sensorName;
    private LocalTime begin;
    private LocalTime end;
}
