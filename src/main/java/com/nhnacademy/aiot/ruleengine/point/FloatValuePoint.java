package com.nhnacademy.aiot.ruleengine.point;

import com.influxdb.client.write.Point;
import com.nhnacademy.aiot.ruleengine.domain.SensorMeasurement;

public class FloatValuePoint implements InfluxdbPoint {
    @Override
    public Point build(SensorMeasurement sensorMeasurement) {
        return InfluxdbPoint.super.build(sensorMeasurement)
                                  .addField("value", Math.round(Float.parseFloat(sensorMeasurement.getValue()) * 10) / 10f);
    }
}
