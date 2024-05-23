package com.nhnacademy.aiot.ruleengine.dto.rule;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RuleInfo {
    private String place;
    private String deviceName;
    private AiMode aiMode;
    private CustomMode customMode;

    public Optional<AiMode> getAiMode() {
        return Optional.ofNullable(aiMode);
    }
}
