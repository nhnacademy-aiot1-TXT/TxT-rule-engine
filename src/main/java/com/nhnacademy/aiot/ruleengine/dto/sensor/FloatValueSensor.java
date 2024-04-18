package com.nhnacademy.aiot.ruleengine.dto.sensor;

import com.influxdb.client.write.Point;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FloatValueSensor extends BaseSensor {
    private Float value;

    @Override
    public Point addValueToInfluxPoint(Point point) {
        return point.addField("value", getValue());
    }
}
