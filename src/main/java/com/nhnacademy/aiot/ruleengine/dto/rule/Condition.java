package com.nhnacademy.aiot.ruleengine.dto.rule;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Condition {
    private ComparisonOperator comparisonOperator;
    private float standardValue;
}
