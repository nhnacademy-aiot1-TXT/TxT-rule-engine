package com.nhnacademy.aiot.ruleengine.dto.message;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class DetailMessage {
    private Object value;
    private String place;
    private String device;
}