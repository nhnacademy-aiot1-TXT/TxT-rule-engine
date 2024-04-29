package com.nhnacademy.aiot.ruleengine.dto.message;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class PredictMessage {
    private Long time;
    private Message indoorTemperature;
    private Message indoorHumidity;
    private Message outdoorTemperature;
    private Message outdoorHumidity;
    private Message totalPeopleCount;
}
