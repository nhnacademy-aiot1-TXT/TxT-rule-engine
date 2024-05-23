package com.nhnacademy.aiot.ruleengine.dto.rule;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ConditionSet {
    private CompareCondition onCondition;
    private CompareCondition offCondition;

    public Optional<CompareCondition> getOffCondition() {
        return Optional.of(offCondition);
    }
}
