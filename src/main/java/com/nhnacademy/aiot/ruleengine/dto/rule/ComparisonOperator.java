package com.nhnacademy.aiot.ruleengine.dto.rule;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ComparisonOperator {
    GREATER_THAN,
    LESS_THAN,
    GREATER_THAN_OR_EQUAL,
    LESS_THAN_OR_EQUAL,
    EQUAL,
    NOT_EQUAL
}
