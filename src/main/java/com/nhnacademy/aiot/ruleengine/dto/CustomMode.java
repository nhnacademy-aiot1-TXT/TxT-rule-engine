package com.nhnacademy.aiot.ruleengine.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AutoMode {
    private List<Condition> onCondition;
    private List<Condition> offCondition;
    private LocalTime timeInterval;
}
