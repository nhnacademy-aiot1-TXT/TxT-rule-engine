package com.nhnacademy.aiot.ruleengine.dto.message;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PredictMessage {
    private Long time;
    private FloatMessage indoorTemperature;
    private FloatMessage indoorHumidity;
    private FloatMessage outdoorTemperature;
    private FloatMessage outdoorHumidity;
    private IntegerMessage totalPeopleCount;
}
