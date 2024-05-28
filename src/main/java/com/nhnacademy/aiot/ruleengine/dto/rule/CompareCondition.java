package com.nhnacademy.aiot.ruleengine.dto.rule;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.function.Predicate;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompareCondition implements Predicate<Float> {
    private ComparisonOperator comparisonOperator;
    private Float standardValue;

    @Override
    public boolean test(Float value) {
        return getComparisonOperator().apply(value, getStandardValue());
    }
}
