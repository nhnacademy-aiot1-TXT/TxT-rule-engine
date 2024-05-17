package com.nhnacademy.aiot.ruleengine.dto.rule;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Condition {
    private ComparisonOperator comparisonOperator;
    private float standardValue;
}
