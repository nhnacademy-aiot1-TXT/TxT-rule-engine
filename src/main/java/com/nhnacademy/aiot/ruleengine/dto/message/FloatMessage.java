package com.nhnacademy.aiot.ruleengine.dto.message;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class FloatMessage {
    private Float value;
    private String device;
    private String place;
}