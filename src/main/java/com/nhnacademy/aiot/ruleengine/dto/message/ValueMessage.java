package com.nhnacademy.aiot.ruleengine.dto.message;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class ValueMessage {
    private String place;
    private String deviceName;
    private Object value;
}
