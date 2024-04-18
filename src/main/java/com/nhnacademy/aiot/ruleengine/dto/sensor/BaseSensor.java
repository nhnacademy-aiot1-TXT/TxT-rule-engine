package com.nhnacademy.aiot.ruleengine.dto.sensor;

import com.influxdb.client.write.Point;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public abstract class BaseSensor {
    protected Long time;
    protected String place;
    protected String topic;
    protected String device;
    protected String measurement;

    public abstract Point addValueToInfluxPoint(Point point);
}
