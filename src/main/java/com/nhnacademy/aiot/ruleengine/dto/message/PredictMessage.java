package com.nhnacademy.aiot.ruleengine.dto.message;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PredictMessage {
    private Long time;
    private ValueMessage indoorTemperature;
    private ValueMessage indoorHumidity;
    private ValueMessage outdoorTemperature;
    private ValueMessage outdoorHumidity;
    private ValueMessage totalPeopleCount;
}
