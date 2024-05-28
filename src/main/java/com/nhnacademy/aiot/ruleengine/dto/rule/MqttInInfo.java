package com.nhnacademy.aiot.ruleengine.dto.rule;

import com.nhnacademy.aiot.ruleengine.constants.Constants;
import lombok.*;

@Getter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MqttInInfo {
    private String mqttUrl;
    private String topic;

    public String getPlace() {
        return getTopic().split("/")[Constants.PLACE_INDEX];
    }

    public String getMeasurement() {
        return getTopic().split("/")[Constants.MEASUREMENT_INDEX];
    }
}
