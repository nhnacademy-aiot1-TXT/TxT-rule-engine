package com.nhnacademy.aiot.ruleengine.dto.rule;

import lombok.*;

import java.util.Optional;

@Getter
@EqualsAndHashCode
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
