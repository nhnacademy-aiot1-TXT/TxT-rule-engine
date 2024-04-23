package com.nhnacademy.aiot.ruleengine.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class IntegerMessage {
    private Integer value;
    private String device;
    private String place;
}