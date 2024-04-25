package com.nhnacademy.aiot.ruleengine.dto.message;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PredictMessage {
    private Long time;
    private FloatMessage temperatureMessage;
    private FloatMessage humidityMessage;
    private IntegerMessage totalPeopleCountMessage;
}
