package com.nhnacademy.aiot.ruleengine.dto.rule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RuleDto {
    private String place;
    private String deviceName;
    private boolean occupancyCheckRequired;
    private AiModeDto aiMode;
    private CustomModeDto customMode;

    public Optional<AiModeDto> getAiMode() {
        return Optional.ofNullable(aiMode);
    }
}

