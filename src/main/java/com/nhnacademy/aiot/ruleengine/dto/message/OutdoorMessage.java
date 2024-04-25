package com.nhnacademy.aiot.ruleengine.dto.message;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class OutdoorMessage {
    private Long time;
    private FloatMessage temperatureMessage;
    private FloatMessage HumidityMessage;
}
