package com.nhnacademy.aiot.ruleengine.dto.sensor;

import com.influxdb.client.write.Point;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class StringValueSensor extends BaseSensor {
    private String value;

    @Override
    public Point addValueToInfluxPoint(Point point) {
        return point.addField("value", getValue());
    }
}
