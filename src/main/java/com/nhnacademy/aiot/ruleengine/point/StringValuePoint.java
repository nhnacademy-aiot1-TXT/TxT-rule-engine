package com.nhnacademy.aiot.ruleengine.point;

import com.influxdb.client.write.Point;
import com.nhnacademy.aiot.ruleengine.domain.SensorMeasurement;

public class StringValuePoint implements InfluxdbPoint {
    @Override
    public Point build(SensorMeasurement sensorMeasurement) {
        return InfluxdbPoint.super.build(sensorMeasurement)
                                  .addField("value", sensorMeasurement.getValue());
    }
}
