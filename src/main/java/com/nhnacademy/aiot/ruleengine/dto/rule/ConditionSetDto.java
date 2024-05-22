package com.nhnacademy.aiot.ruleengine.dto.rule;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ConditionSetDto {
    private CompareConditionDto onCondition;
    private CompareConditionDto offCondition;
}