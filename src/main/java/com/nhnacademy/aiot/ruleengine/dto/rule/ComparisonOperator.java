package com.nhnacademy.aiot.ruleengine.dto.rule;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * floart 값을 비교하는 클래스입니다.
 */
public enum ComparisonOperator implements BiFunction<Float, Float, Boolean> {
    GREATER_THAN(x -> x > 0),
    LESS_THAN(x -> x < 0),
    GREATER_THAN_OR_EQUAL(x -> x >= 0),
    LESS_THAN_OR_EQUAL(x -> x <= 0),
    EQUAL(x -> x == 0),
    NOT_EQUAL(x -> x != 0);

    private final Function<Integer, Boolean> function;

    ComparisonOperator(Function<Integer, Boolean> function) {
        this.function = function;
    }

    @Override
    public Boolean apply(Float value, Float standard) {
        return function.apply(value.compareTo(standard));
    }
}
